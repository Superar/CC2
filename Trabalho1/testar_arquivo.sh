FILE=""
USAGE_MSG="Uso:\n--file [caminho] | -f [caminho] \t Caminho do arquivo em LA a ser executado"

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

mvn -q -f CompiladorLA/ clean package
java -jar CompiladorLA/target/CompiladorLA-1.0-SNAPSHOT-jar-with-dependencies.jar $FILE