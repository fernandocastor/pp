for path in "$@"
do
    java Main $path & 
done
wait
