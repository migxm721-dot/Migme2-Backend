#!/bin/bash
set -e
echo "=== Migme Backend AWS EC2 Setup Script ==="
sudo yum update -y
sudo yum install java-17-amazon-corretto-devel -y
sudo yum install docker -y
sudo systemctl start docker
sudo systemctl enable docker
sudo usermod -aG docker ec2-user
DOCKER_COMPOSE_VERSION="2.23.0"
sudo curl -L "https://github.com/docker/compose/releases/download/v${DOCKER_COMPOSE_VERSION}/docker-compose-$(uname -s)-$(uname -m)" -o /usr/local/bin/docker-compose
sudo chmod +x /usr/local/bin/docker-compose
sudo amazon-linux-extras install nginx1 -y
sudo systemctl start nginx
sudo systemctl enable nginx
sudo yum install certbot python3-certbot-nginx -y
sudo firewall-cmd --permanent --add-service=http
sudo firewall-cmd --permanent --add-service=https
sudo firewall-cmd --permanent --add-port=8080/tcp
sudo firewall-cmd --permanent --add-port=9119/tcp
sudo firewall-cmd --reload
sudo mkdir -p /opt/migme-backend
sudo chown ec2-user:ec2-user /opt/migme-backend
echo "=== Setup Complete ==="
