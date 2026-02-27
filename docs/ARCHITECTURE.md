# Architecture

## Components

- **Spring Boot REST API** (port 8080): HTTP endpoints
- **Netty TCP Server** (port 9119): Real-time binary protocol
- **PostgreSQL**: Persistent data storage
- **Redis**: Session cache and pub/sub
- **Nginx**: Reverse proxy and SSL termination

## Data Flow

1. Android client connects via TCP to port 9119
2. Fusion Protocol packets are parsed by PacketParser
3. NettyServerHandler routes to appropriate service
4. Services process business logic and interact with database
5. Responses serialized back to binary and sent to client
