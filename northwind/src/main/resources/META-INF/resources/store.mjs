const {combineReducers, configureStore} = lib;
import {api as customerApi} from './customers/api.js';
import {api as orderApi} from './orders/api.js';

const reducer = combineReducers({
  [customerApi.reducerPath]: customerApi.reducer,
  [orderApi.reducerPath]: orderApi.reducer
});

export const store = configureStore({
  reducer,
  middleware: getDefaultMiddleware => getDefaultMiddleware()
    .concat(customerApi.middleware)
    .concat(orderApi.middleware)
});
