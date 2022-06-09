import { ActionType, createAction, createReducer } from 'typesafe-actions';

export interface CoffeeState {
    coffee: string,
}

export const initialState: CoffeeState = {
    coffee: "",
};


export const COFFEE_UPDATE = 'coffeeReducer/COFFEE_UPDATE';

// Action Function Definition
export const updateCoffeeItem = createAction(COFFEE_UPDATE)<CoffeeState>();

export const actions = { updateCoffeeItem };
type ReducerActions = ActionType<typeof actions>;

const coffeeReducer = createReducer<CoffeeState, ReducerActions>(
    initialState,
    {
        [COFFEE_UPDATE]: (state, action) => {
            return {
                ...state
            };
        },
    },
);

export default coffeeReducer;
