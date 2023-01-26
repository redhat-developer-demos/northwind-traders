import {api} from './orders/api';
import './App.css';

function App() {
  const {data: orders} = api.endpoints.getRecentOrders.useQuery(undefined, {pollingInterval: 10000});
  return (
    <div className="App">
      {(orders ?? []).map(o => <div key={o.orderId}>{o.orderId}</div>)}
    </div>
  );
}

export default App;
