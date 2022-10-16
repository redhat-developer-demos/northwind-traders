const {createApi, fetchBaseQuery} = lib;

const TAG = 'Customer';

export const api = createApi({
  reducerPath: 'customers',
  baseQuery: fetchBaseQuery({
    baseUrl: '/api/v1/customers',
  }),
  tagTypes: [TAG],
  endpoints: builder => ({
    getCustomers: builder.query({
      query: () => '/',
      providesTags: [TAG],
    })
  })
});
