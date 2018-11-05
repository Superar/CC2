FILE=""
USAGE_MSG="Uso:\n--file [caminho] | -f [caminho] \t Caminho do arquivo .cr a ser executado"

# Argument parsing
while [[ $# -gt 0 ]]
do
    key="$1"
    
    case $key in
        -f|--file)
            if [[ -z $FILE ]]; then
                FILE="$2"
                shift
                shift
            else
                echo -e $USAGE_MSG
                exit 0
            fi
        ;;
        *)
            echo -e $USAGE_MSG
            exit 0
        ;;
    esac
done

if [ -z $FILE ]; then
    echo -e $USAGE_MSG
    exit 0
fi

if [[ ! -f $FILE ]]; then
    echo -e "Arquivo" $FILE "nao existe"
    exit 0
fi 

if ! [ -x "$(command -v pdflatex)" ]; then
    echo -e "pdflatex nao esta instalado"
    exit 0
fi

LATEX_PACKAGES=("longtable" "colortbl" "booktabs" "pgfgantt" "pdflscape" "geometry")

for p in ${LATEX_PACKAGES[@]}
do
    if ! [ "$(kpsewhich $p.sty)" ]; then
    echo -e "Pacote $p nao esta instalado"
    exit 0
fi
done

mvn -q -f chronological/ clean package
java -jar chronological/target/chronological-0.1-jar-with-dependencies.jar $FILE ${FILE%.*}.tex

if grep -q "documentclass" "${FILE%.*}.tex"; then
    pdflatex -output-directory $(dirname $FILE) ${FILE%.*}.tex > /dev/null 2> /dev/null
    rm ${FILE%.*}.log ${FILE%.*}.aux
fi