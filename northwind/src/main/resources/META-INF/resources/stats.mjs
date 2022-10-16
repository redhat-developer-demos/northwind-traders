const {html} = lib;
import {POLLING_INTERVAL_MS} from './app.mjs';
import {Card} from './components.mjs'
import {BankNotesOutline, ShoppingCart, TruckIcon, UserGroup} from './icons.mjs';
import {api as customerApi} from './customers/api.js';
import {api as orderApi} from './orders/api.js';

const StatCard = ({Icon, title, value, color = 'blue'}) => html`
  <${Card} className='flex items-center'>
    <div className=${`p-3 mr-4 text-${color}-500 bg-${color}-100 rounded-full`}>
      <${Icon} className='h-5 w-5' />
    </div>
    <div>
      <p className='mb-1 text-sm font-semibold text-gray-600 overflow-hidden text-ellipsis'>
        ${title}
      </p>
      <p className='text-lg font-semibold text-gray-700'>
        ${value}
      </p>
    </div>
  </${Card}>
`;

export const Stats = () => {
  const {data: allCustomers} = customerApi.endpoints.getCustomers.useQuery(undefined, {pollingInterval: POLLING_INTERVAL_MS});
  const {data: pendingShipments} = orderApi.endpoints.getPendingShipmentCount.useQuery(undefined, {pollingInterval: POLLING_INTERVAL_MS});
  const {data: revenue} = orderApi.endpoints.getTotalRevenue.useQuery(undefined, {pollingInterval: POLLING_INTERVAL_MS});
  return html`
    <div className='grid gap-5 mb-5 md:grid-cols-2 xl:grid-cols-4'>
      <${StatCard}
          title='Customers' value=${allCustomers?.length ?? 0} Icon=${UserGroup} color='purple' />
      <${StatCard}
          title='Orders' value=${revenue?.orderCount} Icon=${ShoppingCart} color='blue' />
      <${StatCard}
          title='Revenue'
          value=${revenue?.totalRevenue.toLocaleString('en-US', {style: 'currency', currency: 'USD'})}
          Icon=${BankNotesOutline} color='green' />
      <${StatCard} title='Pending shipments' value=${pendingShipments} Icon=${TruckIcon} color='orange' />
    </div>
  `;
};
