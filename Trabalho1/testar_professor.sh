TIPO=""
EXEC="java -jar CompiladorLA/target/CompiladorLA-1.0-SNAPSHOT-jar-with-dependencies.jar"
ARQUIVOS_TESTE="casosDeTesteT1"
RA="587265"

USAGE_MSG="Uso:\n--sintatico | -s \t Teste sintatico\n--semantico | -m \t Teste semantico\n--gerador | -g \t Testa gerador de codigo\n--tudo | -t \t Testa tudo"

# Argument parsing
while [[ $# -gt 0 ]]
do
    key="$1"
    
    case $key in
        -s|--sintatico)
            if [[ -z $TIPO ]]; then
                TIPO="sintatico"
                shift
            else
                echo -e $USAGE_MSG
                exit 0
            fi
        ;;
        -m|--semantico)
            if [[ -z $TIPO ]]; then
                TIPO="semantico"
                shift
            else
                echo -e $USAGE_MSG
                exit 0
            fi
        ;;
        -g|--gerador)
            if [[ -z $TIPO ]]; then
                TIPO="gerador"
                shift
            else
                echo -e $USAGE_MSG
                exit 0
            fi
        ;;
        -t|--tudo)
            if [[ -z $TIPO ]]; then
                TIPO="tudo"
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

if [ -z $TIPO ]; then
    echo -e $USAGE_MSG
    exit 0
fi

if [ ! -d "temp" ]; then
    mkdir temp
fi

mvn -q -f CompiladorLA/ clean package
java -jar CorretorTrabalho1/CorretorTrabalho1.jar "$EXEC" gcc temp $ARQUIVOS_TESTE "$RA" $TIPO