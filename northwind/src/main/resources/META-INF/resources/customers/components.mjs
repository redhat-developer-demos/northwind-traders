const {html} = lib;
import {POLLING_INTERVAL_MS} from '../app.mjs';
import {Card} from '../components.mjs';
import {api} from './api.js';

const Avatar = ({name}) => {
  const initials = name.split(' ').map(n => n[0]).join('').toUpperCase();
  return html`
    <div className='inline-flex overflow-hidden relative justify-center items-center w-10 h-10 bg-purple-100 rounded-full'>
      <span className='font-medium text-purple-600'>
        ${initials.length > 2 ? initials.substring(0, 2) : initials}
      </span>
    </div>
  `;
};

const CustomerEntries = ({}) => {
  const {data: customers} = api.endpoints.getCustomers.useQuery(undefined, {pollingInterval: POLLING_INTERVAL_MS});
  return customers && Array.from(customers).map(c => html`
    <li key=${c.id} className='p-2 flex items-center border-b last:border-b-0 border-solid whitespace-nowrap'>
      <${Avatar} name=${c.companyName} />
      <div className='ml-2 overflow-hidden '>
        <p className='text-sm font-semibold text-gray-600 overflow-hidden text-ellipsis'>
          ${c.companyName}
        </p>
        <p className='text-sm ext-gray-700'>
          ${c.city}, ${c.country}
        </p>
      </div>
    </li>
  `);
};

export const CustomersCard = ({className}) => {
  return html`
    <${Card} className=${`flex flex-col ${className}`} title='Customers'>
      <div className='flex-1 overflow-hidden relative'>
        <ul className='overflow-auto absolute top-0 bottom-0 left-0 right-0'>
          <${CustomerEntries} />
        </ul>
      </div>
    </${Card}>
  `;
};
