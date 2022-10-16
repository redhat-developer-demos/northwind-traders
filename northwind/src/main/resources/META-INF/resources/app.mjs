import './lib/tailwind.min-3.1.8.js';
import './lib/bundle.js';
const {Provider, html} = lib;

import {Header, Main} from './components.mjs';
import {LightHouse} from './icons.mjs';
import {Stats} from './stats.mjs';
import {store} from './store.mjs';
import {CustomersCard} from './customers/components.mjs';
import {RecentOrders} from './orders/components.mjs';

export const POLLING_INTERVAL_MS = 5000;
export const toCurrency = (amount = 0) =>
  amount.toLocaleString('en-US', {style: 'currency', currency: 'USD'});

const App = () => html`
  <div className='flex h-screen bg-gray-100'>
    <div className='flex flex-col flex-1 w-full'>
      <${Header}>
        <div className='flex items-center'>
          <${LightHouse} className='w-6 h-6 mr-1' />
          <h1 className='text-lg font-bold text-gray-800'>
            Northwind traders
          </h1>
        </div>
      </${Header}>
      <${Main}>
        <${Stats} />
        <div className='flex-1 grid gap-5 grid-cols-1 lg:grid-cols-4'>
          <${CustomersCard} className='h-72 lg:h-auto'/>
          <${RecentOrders} className='h-72 lg:h-auto lg:col-span-3'/>
        </div>
      </${Main}>
    </div>
  </div>
`;

const root = lib.createRoot(document.querySelector('.northwind.root'))
root.render(html`<${Provider} store=${store}><${App} /></${Provider}>`);
