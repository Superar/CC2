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

FILE_BASENAME=$(basename $FILE)

./gera_tex.sh -f $FILE
./gera_pdf.sh -f "saidaGerada/${FILE_BASENAME%.*}.tex"
