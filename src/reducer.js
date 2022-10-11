const reducer = (state, { type, payload }) => {
    switch (type) {

    case "ADD_ITEM":
        return { ...state, people: [...state.people, {...payload}] }

    case "X": 
        return{...state, people: state.people.filter(person => person.id !== payload.id)}

    default:
        return state
    }
}


export default reducer;