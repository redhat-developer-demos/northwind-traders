import {createApi, fetchBaseQuery} from '@reduxjs/toolkit/dist/query/react';

const TAG = 'Order';

export const api = createApi({
  reducerPath: 'orders',
  baseQuery: fetchBaseQuery({
    baseUrl: `/api/v1`,
  }),
  tagTypes: [TAG],
  endpoints: builder => ({
    getRecentOrders: builder.query({query: () => '/orders?sort=orderDate&direction=Descending&limit=10'})
  })
});

api.sendOrder = ({orderId}) => fetch(`/api/v1/mail/orders/${orderId}`, {
  method: 'POST'
});
