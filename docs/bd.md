### Entities
- Request(_TOKEN_, date, state, municipality)
- Residue(_ID_, desc, nome, peso, volume)
- StateChange(_TOKEN_, _DATE_, state)

### Relations

- Request (1) <-> Residue (N)
- Request (1) <-> StateChange(N)