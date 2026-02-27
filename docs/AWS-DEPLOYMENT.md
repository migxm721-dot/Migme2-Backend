# AWS EC2 Deployment Guide

## Prerequisites
- AWS EC2 instance (t3.medium or larger)
- Amazon Linux 2
- Elastic IP assigned
- Domain pointed to EC2 IP

## Steps

1. Run setup script:
```bash
bash infrastructure/aws/ec2-setup.sh
```

2. Clone repository:
```bash
git clone https://github.com/migxm721-dot/Migme2-Backend.git /opt/migme-backend
```

3. Configure environment:
```bash
cp .env.example .env
nano .env
```

4. Setup SSL:
```bash
sudo certbot --nginx -d api.migxchat.net
```

5. Start services:
```bash
cd docker && docker-compose -f docker-compose.yml -f docker-compose.prod.yml up -d
```
