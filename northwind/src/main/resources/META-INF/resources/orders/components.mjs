const {html} = lib;
import {POLLING_INTERVAL_MS, toCurrency} from '../app.mjs';
import {Card, Table} from '../components.mjs';
import {api} from './api.js';

const OrderRows = ({}) => {
  const {data: orders} = api.endpoints.getRecentOrders.useQuery(undefined, {pollingInterval: POLLING_INTERVAL_MS});
  return orders && Array.from(orders).map(o => html`
    <${Table.Row} key=${o.orderId}>
      <${Table.Cell}>${o.orderId}</${Table.Cell}>
      <${Table.Cell}>${new Date(o.orderDate).toLocaleDateString()}</${Table.Cell}>
      <${Table.Cell}>${o.customer.companyName}</${Table.Cell}>
      <${Table.Cell} className='text-right'>${
        toCurrency(o.orderDetails.reduce((acc, od) => acc + (od.unitPrice * od.quantity), 0))
      }</${Table.Cell}>
    </${Table.Row}>
  `);
};

export const RecentOrders = ({className}) => {
  return html`
    <${Card} className=${`flex flex-col ${className}`} title='Recent orders'>
      <div className='flex-1 overflow-hidden relative'>
        <div className='overflow-auto absolute top-0 bottom-0 left-0 right-0'>
          <${Table} className='' title='Recent orders'>
            <${Table.Head} className='sticky top-0 bg-white text-slate-400 border-b'>
              <${Table.Column}>Order #</${Table.Column}>
              <${Table.Column}>Date</${Table.Column}>
              <${Table.Column}>Customer</${Table.Column}>
              <${Table.Column} className='text-right'>Total</${Table.Column}>
            </{Table.Head}>
            <${Table.Body}>
              <${OrderRows} />
            </${Table.Body}>
          </${Table}>
        </div>
      </div>
    </${Card}>
  `;
};
