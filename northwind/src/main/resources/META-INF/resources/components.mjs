const {html} = lib;

export const Header = ({children}) => (html`
  <header className="z-10 py-4 bg-white shadow-md">
    <div className="mx-6 flex items-center justify-between h-full text-gray-700">
      ${children}
    </div>
  </header>
`);

export const Main = ({children}) => (html`
  <main className="h-full overflow-y-auto">
    <div className="flex flex-col mx-5 py-5 lg:h-full">
      ${children}
    </div>
  </main>
`);

export const Card = ({title, className = '', children}) => {
  const TheCard = ({children}) => html`
    <div className=${`min-w-0 p-4 bg-white shadow border-b border-gray-50 sm:rounded-lg ${className}`}>
      ${children}
    </div>
  `;
  if (title) {
    return html`
      <${TheCard}>
        <h4 className="mb-4 font-semibold text-gray-600">
          ${title}
        </h4>
        ${children}
      </${TheCard}>
    `;
  }
  return html`<${TheCard}>${children}</${TheCard}>`;
};

export const Table = ({className = '', children}) => (html`
  <table className=${`min-w-full ${className}`}>
    ${children}
  </table>
`);

Table.Head = ({className = '', children}) => (html`
  <thead>
    <tr className=${className}>${children}</tr>
  </thead>
`);

Table.Column = ({className = '', children}) => (html`
  <th className=${`px-3 py-2 text-left leading-4 font-semibold text-sm text-gray-600 uppercase ${className}`}>
    ${children}
  </th>
`);

Table.Body = ({children}) => (html`
  <tbody className='divide-y divide-gray-300'>${children}</tbody>
`);

Table.Row = ({children, className = ''}) => (html`
  <tr className=${`even:bg-opacity-50 even:bg-gray-200 ${className}`}>${children}</tr>
`);

Table.Cell = ({
  children, textColor = 'text-gray-800', textSize = 'text-sm', className = '', ...properties
}) => (html`
  <td
    className=${`px-3 py-2 ${textColor} ${textSize} ${className}`}
    ...${properties}
  >${children}</td>
`);
