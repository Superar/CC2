FILE=""
USAGE_MSG="Uso:\n--file [caminho] | -f [caminho] \t Caminho do arquivo .tex a gerar o PDF"

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

mkdir -p saidaGerada

FILE_BASENAME=$(basename $FILE)

if grep -q "documentclass" "$FILE"; then
    pdflatex -output-directory saidaGerada $FILE > /dev/null 2> /dev/null
    rm saidaGerada/${FILE_BASENAME%.*}.log saidaGerada/${FILE_BASENAME%.*}.aux
fi