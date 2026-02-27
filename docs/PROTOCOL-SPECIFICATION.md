# Fusion Protocol Specification

## Overview
Binary TCP protocol used for real-time communication.

## Packet Structure
```
[Type: 1 byte][Length: 4 bytes][Sequence: 4 bytes][Fields: variable]
```

## Field Structure
```
[Field ID: 2 bytes][Field Type: 1 byte][Length/Value: variable]
```

## Packet Types
- 1: LOGIN
- 2: LOGIN_OK
- 3: MESSAGE
- 4: PRESENCE
- 15: JOIN_CHATROOM
- 16: LEAVE_CHATROOM
- 21: MESSAGE_STATUS_EVENT
- 34: PING
- 35: PONG
- 36: ERROR

## Field Types
- 1: BYTE
- 2: SHORT
- 3: INT
- 4: LONG
- 5: STRING (2-byte length prefix + UTF-8)
- 6: BOOLEAN
- 7: BYTE_ARRAY (4-byte length prefix)
