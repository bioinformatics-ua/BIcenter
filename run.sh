base_path=$(pwd)

## Pentaho execution
cd "$base_path/deployment/pentaho"
chmod +x deploy_carte.sh
./deploy_carte.sh

if [ $? -eq 0 ]; then
    echo "Pentaho services are up and running\n"
else
    echo "Pentaho services did not started successfully"
    exit 1
fi