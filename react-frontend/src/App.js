import {api} from './orders/api';

function App() {
  const {data: orders} = api.endpoints.getRecentOrders.useQuery(undefined, {pollingInterval: 10000});
  return (
    <div className="react-fronted">
      <h1>Latest Orders</h1>
      {(orders ?? []).map(({orderId, customer: {companyName}}) =>
        <div key={orderId} className='order'>{orderId} - {companyName}</div>
      )}
    </div>
  );
}

export default App;
