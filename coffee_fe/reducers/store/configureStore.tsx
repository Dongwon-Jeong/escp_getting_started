import { createStore } from 'redux';
import rootReducer from './rootReducer';
import { composeWithDevTools } from 'redux-devtools-extension';
import { createWrapper } from 'next-redux-wrapper';

// store 생성
const configureStore = () => {
    // const logger = createLogger();
    // const middlewares = [logger];

    // const enhancer = composeWithDevTools(applyMiddleware(...middlewares))
    const enhancer = composeWithDevTools();

    const store = createStore(rootReducer, enhancer);

    // 사가설정 추가안함
    // store.sagaTask = sagaMiddleware.run(null)
    return store;
};

const wrapper = createWrapper(configureStore, { debug: false });

export default wrapper;
