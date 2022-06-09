import { combineReducers, Reducer, Action } from 'redux';
import { HYDRATE } from 'next-redux-wrapper';
import CoffeeReducer, {CoffeeState} from "../coffeeReducer";

export interface State {
    coffee: CoffeeState;
}

const rootReducer: Reducer<any, Action> = (state: State, action: any) => {
    switch (action.type) {
        case HYDRATE:
            return action.payload;

        default: {
            const combineReducer = combineReducers({
                coffee: CoffeeReducer
            });

            return combineReducer(state, action);
        }
    }
};

export default rootReducer;

export type RootState = ReturnType<typeof rootReducer>;
