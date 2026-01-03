import React, { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import { getAllUsers, getAllTransactions, freezeWallet, unfreezeWallet } from '../api';

function AdminDashboard() {
  const [users, setUsers] = useState([]);
  const [transactions, setTransactions] = useState([]);
  const [loading, setLoading] = useState(true);
  const [activeTab, setActiveTab] = useState('users');
  const navigate = useNavigate();

  useEffect(() => {
    fetchData();
  }, []);

  const fetchData = async () => {
    try {
      const usersRes = await getAllUsers();
      const transRes = await getAllTransactions();
      setUsers(usersRes.data);
      setTransactions(transRes.data);
    } catch (err) {
      console.error('Error fetching data:', err);
    } finally {
      setLoading(false);
    }
  };

  const handleFreezeWallet = async (walletId) => {
    try {
      await freezeWallet(walletId);
      alert('Wallet frozen successfully');
      await fetchData();
    } catch (err) {
      alert('Failed to freeze wallet');
    }
  };

  const handleUnfreezeWallet = async (walletId) => {
    try {
      await unfreezeWallet(walletId);
      alert('Wallet unfrozen successfully');
      await fetchData();
    } catch (err) {
      alert('Failed to unfreeze wallet');
    }
  };

  const handleLogout = () => {
    localStorage.clear();
    navigate('/login');
  };

  const riskyTransactions = transactions.filter(tx => tx.riskFlag);

  if (loading) return <div className="loading">Loading...</div>;

  return (
    <div className="dashboard admin-dashboard">
      <header className="dashboard-header">
        <h1>Admin Dashboard</h1>
        <div className="header-actions">
          <span className="user-email">{localStorage.getItem('email')}</span>
          <button onClick={handleLogout} className="btn-secondary">Logout</button>
        </div>
      </header>

      <div className="admin-tabs">
        <button 
          className={activeTab === 'users' ? 'active' : ''}
          onClick={() => setActiveTab('users')}
        >
          Users ({users.length})
        </button>
        <button 
          className={activeTab === 'transactions' ? 'active' : ''}
          onClick={() => setActiveTab('transactions')}
        >
          All Transactions ({transactions.length})
        </button>
        <button 
          className={activeTab === 'risky' ? 'active' : ''}
          onClick={() => setActiveTab('risky')}
        >
          Risky Transactions ({riskyTransactions.length})
        </button>
      </div>

      <div className="dashboard-content">
        {activeTab === 'users' && (
          <section className="users-section">
            <h2>User Management</h2>
            <div className="users-table">
              <table>
                <thead>
                  <tr>
                    <th>ID</th>
                    <th>Email</th>
                    <th>Role</th>
                    <th>Created At</th>
                  </tr>
                </thead>
                <tbody>
                  {users.map((user) => (
                    <tr key={user.id}>
                      <td>{user.id}</td>
                      <td>{user.email}</td>
                      <td><span className={`role-badge ${user.role.toLowerCase()}`}>{user.role}</span></td>
                      <td>{new Date(user.createdAt).toLocaleString()}</td>
                    </tr>
                  ))}
                </tbody>
              </table>
            </div>
          </section>
        )}

        {activeTab === 'transactions' && (
          <section className="transactions-section">
            <h2>All Transactions</h2>
            <div className="transaction-table">
              <table>
                <thead>
                  <tr>
                    <th>ID</th>
                    <th>User</th>
                    <th>Wallet ID</th>
                    <th>Currency</th>
                    <th>Amount</th>
                    <th>Type</th>
                    <th>Risk</th>
                    <th>Date</th>
                  </tr>
                </thead>
                <tbody>
                  {transactions.map((tx) => (
                    <tr key={tx.id}>
                      <td>{tx.id}</td>
                      <td>{tx.wallet.user.email}</td>
                      <td>{tx.wallet.id}</td>
                      <td>{tx.wallet.currency}</td>
                      <td>{parseFloat(tx.amount).toFixed(2)}</td>
                      <td>{tx.type}</td>
                      <td>
                        {tx.riskFlag && <span className="risk-badge">⚠ High Risk</span>}
                      </td>
                      <td>{new Date(tx.createdAt).toLocaleString()}</td>
                    </tr>
                  ))}
                </tbody>
              </table>
            </div>
          </section>
        )}

        {activeTab === 'risky' && (
          <section className="transactions-section">
            <h2>Risky Transactions</h2>
            <div className="transaction-table">
              <table>
                <thead>
                  <tr>
                    <th>ID</th>
                    <th>User</th>
                    <th>Wallet ID</th>
                    <th>Currency</th>
                    <th>Amount</th>
                    <th>Type</th>
                    <th>Date</th>
                    <th>Actions</th>
                  </tr>
                </thead>
                <tbody>
                  {riskyTransactions.map((tx) => (
                    <tr key={tx.id} className="risky-row">
                      <td>{tx.id}</td>
                      <td>{tx.wallet.user.email}</td>
                      <td>{tx.wallet.id}</td>
                      <td>{tx.wallet.currency}</td>
                      <td className="amount-highlight">{parseFloat(tx.amount).toFixed(2)}</td>
                      <td>{tx.type}</td>
                      <td>{new Date(tx.createdAt).toLocaleString()}</td>
                      <td>
                        {tx.wallet.status === 'ACTIVE' ? (
                          <button 
                            onClick={() => handleFreezeWallet(tx.wallet.id)}
                            className="btn-danger"
                          >
                            Freeze Wallet
                          </button>
                        ) : (
                          <button 
                            onClick={() => handleUnfreezeWallet(tx.wallet.id)}
                            className="btn-success"
                          >
                            Unfreeze Wallet
                          </button>
                        )}
                      </td>
                    </tr>
                  ))}
                </tbody>
              </table>
            </div>
          </section>
        )}
      </div>
    </div>
  );
}

export default AdminDashboard;