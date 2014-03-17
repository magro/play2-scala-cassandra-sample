apt-get update
apt-get install -y openjdk-7-jdk
apt-get install -y curl
echo "deb http://debian.datastax.com/community stable main" | sudo tee -a /etc/apt/sources.list.d/cassandra.sources.list
curl -L http://debian.datastax.com/debian/repo_key | sudo apt-key add -
apt-get update
apt-get install -y dsc20
sed 's|rpc_address: localhost|rpc_address: 0.0.0.0|g' -i /etc/cassandra/cassandra.yaml
/etc/init.d/cassandra restart
