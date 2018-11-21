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

mkdir -p saidaGerada

FILE_BASENAME=$(basename $FILE)

if [[ ! -f chronological/target/chronological-0.1-jar-with-dependencies.jar ]]; then
    mvn -q -f chronological/ clean package
fi

java -jar chronological/target/chronological-0.1-jar-with-dependencies.jar $FILE saidaGerada/${FILE_BASENAME%.*}.tex
