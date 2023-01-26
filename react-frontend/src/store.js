import {combineReducers, configureStore} from '@reduxjs/toolkit';
import {api as orderApi} from './orders/api';

const reducer = combineReducers({
  [orderApi.reducerPath]: orderApi.reducer
});

export const store = configureStore({
  reducer,
  middleware: getDefaultMiddleware => getDefaultMiddleware()
    .concat(orderApi.middleware)
});
