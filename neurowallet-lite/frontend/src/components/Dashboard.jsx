import React, { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import { getWallets, createWallet, getTransactions, createTransaction } from '../api';

function Dashboard() {
  const [wallets, setWallets] = useState([]);
  const [transactions, setTransactions] = useState([]);
  const [loading, setLoading] = useState(true);
  const [showWallets, setShowWallets] = useState(true);
  const [showNewTransaction, setShowNewTransaction] = useState(false);
  const [showTransactionHistory, setShowTransactionHistory] = useState(false);
  const [showNewWallet, setShowNewWallet] = useState(false);
  const [showImageUpload, setShowImageUpload] = useState(false);
  const [draggedWallet, setDraggedWallet] = useState(null);
  const [transactionData, setTransactionData] = useState({
    fromWallet: null,
    toWallet: null,
    amount: '',
    type: 'DEPOSIT',
    description: ''
  });
  const [newCurrency, setNewCurrency] = useState('MYR');
  const [profileImage, setProfileImage] = useState(null);
  const navigate = useNavigate();

  const walletColors = {
    MYR: 'linear-gradient(135deg, rgba(0, 56, 147, 0.3) 0%, rgba(255, 209, 0, 0.3) 50%, rgba(204, 0, 1, 0.3) 100%)',
    SGD: 'linear-gradient(135deg, rgba(239, 51, 64, 0.3) 0%, rgba(255, 255, 255, 0.3) 100%)',
    USD: 'linear-gradient(135deg, rgba(178, 34, 52, 0.3) 0%, rgba(255, 255, 255, 0.3) 50%, rgba(60, 59, 110, 0.3) 100%)'
  };

  useEffect(() => {
    fetchData();
  }, []);

  const fetchData = async () => {
    try {
      const walletsRes = await getWallets();
      setWallets(walletsRes.data);
      const transRes = await getTransactions();
      setTransactions(transRes.data);
    } catch (err) {
      console.error('Error fetching data:', err);
    } finally {
      setLoading(false);
    }
  };

  const handleCreateWallet = async (e) => {
    e.preventDefault();
    try {
      await createWallet(newCurrency);
      await fetchData();
      setShowNewWallet(false);
      setNewCurrency('MYR');
    } catch (err) {
      alert(err.response?.data?.error || 'Failed to create wallet');
    }
  };

  const handleTransaction = async () => {
    try {
      const { fromWallet, toWallet, amount, type, description } = transactionData;
      
      if (!amount || parseFloat(amount) <= 0) {
        alert('Please enter a valid amount');
        return;
      }

      let requestData = {
        amount: parseFloat(amount),
        type: type,
        description: description
      };

      if (type === 'DEPOSIT') {
        if (!toWallet) {
          alert('Please select a destination wallet');
          return;
        }
        requestData.walletId = toWallet.id;
      } else if (type === 'WITHDRAWAL') {
        if (!fromWallet) {
          alert('Please select a source wallet');
          return;
        }
        requestData.walletId = fromWallet.id;
      } else if (type === 'TRANSFER') {
        if (!fromWallet || !toWallet) {
          alert('Please select both source and destination wallets');
          return;
        }
        requestData.walletId = fromWallet.id;
        requestData.toWalletId = toWallet.id;
      }

      await createTransaction(requestData);
      await fetchData();
      setShowNewTransaction(false);
      setTransactionData({
        fromWallet: null,
        toWallet: null,
        amount: '',
        type: 'DEPOSIT',
        description: ''
      });
    } catch (err) {
      alert(err.response?.data?.error || 'Transaction failed');
    }
  };

  const handleDragStart = (wallet, e) => {
    setDraggedWallet(wallet);
    e.dataTransfer.effectAllowed = 'move';
  };

  const handleDragOver = (e) => {
    e.preventDefault();
    e.dataTransfer.dropEffect = 'move';
  };

  const handleDropFrom = (e) => {
    e.preventDefault();
    if (draggedWallet) {
      setTransactionData({ ...transactionData, fromWallet: draggedWallet });
      setDraggedWallet(null);
    }
  };

  const handleDropTo = (e) => {
    e.preventDefault();
    if (draggedWallet) {
      setTransactionData({ ...transactionData, toWallet: draggedWallet });
      setDraggedWallet(null);
    }
  };

  const handleImageUpload = async (e) => {
    const file = e.target.files[0];
    if (file) {
      const formData = new FormData();
      formData.append('file', file);
      
      try {
        const token = localStorage.getItem('token');
        console.log('Uploading file:', file.name, 'Size:', file.size, 'Type:', file.type);
        console.log('Token present:', !!token);
        
        const response = await fetch('http://localhost:8080/api/files/upload', {
          method: 'POST',
          headers: {
            'Authorization': `Bearer ${token}`
          },
          body: formData
        });
        
        console.log('Response status:', response.status);
        const data = await response.json();
        console.log('Response data:', data);
        
        if (response.ok && data.success) {
          setProfileImage(data.url);
          alert('Profile image uploaded successfully!');
        } else {
          console.error('Upload failed:', data);
          alert(data.error || 'Failed to upload image');
        }
      } catch (err) {
        console.error('Upload error:', err);
        alert('Failed to upload image: ' + err.message);
      }
    }
  };

  const handleLogout = () => {
    localStorage.clear();
    navigate('/login');
  };

  if (loading) return <div className="loading">Loading...</div>;

  return (
    <div className="tiling-dashboard">
      {/* Sidebar - Always Expanded */}
      <div className="sidebar-tile expanded">
        <div className="sidebar-header">
          <div className="profile-section">
            {profileImage ? (
              <img src={`http://localhost:8080${profileImage}`} alt="Profile" className="profile-img" />
            ) : (
              <div className="profile-placeholder">
                {localStorage.getItem('email').charAt(0).toUpperCase()}
              </div>
            )}
            <div className="profile-info">
              <div className="user-email">{localStorage.getItem('email')}</div>
            </div>
          </div>
        </div>

        <nav className="sidebar-nav">
          <button 
            className={`nav-item ${showWallets ? 'active' : ''}`}
            onClick={() => {
              setShowWallets(!showWallets);
            }}
          >
            <span className="icon">💼</span>
            <span className="label">My Wallets</span>
          </button>

          <button 
            className="nav-item"
            onClick={() => {
              setShowNewTransaction(true);
            }}
          >
            <span className="icon">💸</span>
            <span className="label">New Transaction</span>
          </button>

          <button 
            className={`nav-item ${showTransactionHistory ? 'active' : ''}`}
            onClick={() => {
              setShowTransactionHistory(true);
            }}
          >
            <span className="icon">📊</span>
            <span className="label">Transaction History</span>
          </button>

          <button 
            className="nav-item"
            onClick={() => {
              setShowNewWallet(true);
            }}
          >
            <span className="icon">➕</span>
            <span className="label">Add Wallet</span>
          </button>

          <button 
            className="nav-item"
            onClick={() => {
              setShowImageUpload(true);
            }}
          >
            <span className="icon">📷</span>
            <span className="label">Upload Profile Image</span>
          </button>
        </nav>

        <button className="nav-item logout-btn" onClick={handleLogout}>
          <span className="icon">🚪</span>
          <span className="label">Logout</span>
        </button>
      </div>

      {/* Main Content Area */}
      <div className="tiles-container">
        {/* Individual Wallet Tiles */}
        {showWallets && wallets.map((wallet, idx) => (
          <div 
            key={wallet.id} 
            className="tile wallet-tile"
            draggable
            onDragStart={(e) => handleDragStart(wallet, e)}
            style={{
              background: walletColors[wallet.currency],
              backdropFilter: 'blur(10px)',
              border: '2px solid rgba(0,0,0,0.1)',
              animationDelay: `${idx * 0.1}s`
            }}
          >
            <div className="wallet-tile-header">
              <span className="currency-code">{wallet.currency}</span>
              <span className={`status-badge ${wallet.status.toLowerCase()}`}>
                {wallet.status}
              </span>
            </div>
            <div className="wallet-balance">
              {parseFloat(wallet.balance).toFixed(2)}
            </div>
            <div className="wallet-currency-name">
              {wallet.currency === 'MYR' && '🇲🇾 Malaysian Ringgit'}
              {wallet.currency === 'SGD' && '🇸🇬 Singapore Dollar'}
              {wallet.currency === 'USD' && '🇺🇸 US Dollar'}
            </div>
          </div>
        ))}

        {/* New Transaction Tile */}
        {showNewTransaction && (
          <div className="tile transaction-tile">
            <div className="tile-header">
              <h3>New Transaction</h3>
              <button className="close-btn" onClick={() => setShowNewTransaction(false)}>✕</button>
            </div>

            <div className="transaction-type-selector">
              <button 
                className={transactionData.type === 'DEPOSIT' ? 'active' : ''}
                onClick={() => setTransactionData({ ...transactionData, type: 'DEPOSIT', fromWallet: null })}
              >
                Deposit
              </button>
              <button 
                className={transactionData.type === 'WITHDRAWAL' ? 'active' : ''}
                onClick={() => setTransactionData({ ...transactionData, type: 'WITHDRAWAL', toWallet: null })}
              >
                Withdrawal
              </button>
              <button 
                className={transactionData.type === 'TRANSFER' ? 'active' : ''}
                onClick={() => setTransactionData({ ...transactionData, type: 'TRANSFER' })}
              >
                Transfer
              </button>
            </div>

            <div className="transaction-flow">
              {/* FROM Section */}
              <div 
                className="drop-zone from-zone"
                onDragOver={handleDragOver}
                onDrop={handleDropFrom}
              >
                <div className="zone-label">FROM</div>
                {transactionData.fromWallet ? (
                  <div className="selected-wallet" style={{ background: walletColors[transactionData.fromWallet.currency] }}>
                    <div>{transactionData.fromWallet.currency}</div>
                    <div className="wallet-balance-small">{parseFloat(transactionData.fromWallet.balance).toFixed(2)}</div>
                    <button className="remove-btn" onClick={() => setTransactionData({ ...transactionData, fromWallet: null })}>✕</button>
                  </div>
                ) : transactionData.type === 'DEPOSIT' ? (
                  <div className="mock-source">
                    <div>🏦</div>
                    <div className="mock-label">External Bank</div>
                  </div>
                ) : (
                  <div className="drop-placeholder">Drag wallet here</div>
                )}
              </div>

              {/* Amount & Description */}
              <div className="transaction-details">
                <input
                  type="number"
                  step="0.01"
                  placeholder="Amount"
                  value={transactionData.amount}
                  onChange={(e) => setTransactionData({ ...transactionData, amount: e.target.value })}
                  className="amount-input"
                />
                <input
                  type="text"
                  placeholder="Description (optional)"
                  value={transactionData.description}
                  onChange={(e) => setTransactionData({ ...transactionData, description: e.target.value })}
                  className="description-input"
                />
              </div>

              {/* TO Section */}
              <div 
                className="drop-zone to-zone"
                onDragOver={handleDragOver}
                onDrop={handleDropTo}
              >
                <div className="zone-label">TO</div>
                {transactionData.toWallet ? (
                  <div className="selected-wallet" style={{ background: walletColors[transactionData.toWallet.currency] }}>
                    <div>{transactionData.toWallet.currency}</div>
                    <div className="wallet-balance-small">{parseFloat(transactionData.toWallet.balance).toFixed(2)}</div>
                    <button className="remove-btn" onClick={() => setTransactionData({ ...transactionData, toWallet: null })}>✕</button>
                  </div>
                ) : transactionData.type === 'WITHDRAWAL' ? (
                  <div className="mock-source">
                    <div>💳</div>
                    <div className="mock-label">Payment Gateway</div>
                  </div>
                ) : (
                  <div className="drop-placeholder">Drag wallet here</div>
                )}
              </div>
            </div>

            <button className="submit-transaction-btn" onClick={handleTransaction}>
              Submit Transaction
            </button>
          </div>
        )}

        {/* Transaction History Tile */}
        {showTransactionHistory && (
          <div className="tile history-tile">
            <div className="tile-header">
              <h3>Transaction History</h3>
              <button className="close-btn" onClick={() => setShowTransactionHistory(false)}>✕</button>
            </div>
            <div className="history-table-container">
              <table>
                <thead>
                  <tr>
                    <th>Date</th>
                    <th>Type</th>
                    <th>Amount</th>
                    <th>Currency</th>
                    <th>Risk</th>
                    <th>Description</th>
                  </tr>
                </thead>
                <tbody>
                  {transactions.map((tx) => (
                    <tr key={tx.id}>
                      <td>{new Date(tx.createdAt).toLocaleDateString()}</td>
                      <td><span className={`type-badge ${tx.type.toLowerCase()}`}>{tx.type}</span></td>
                      <td>{parseFloat(tx.amount).toFixed(2)}</td>
                      <td>{tx.wallet.currency}</td>
                      <td>
                        {tx.riskFlag && <span className="risk-badge">⚠ High Risk</span>}
                      </td>
                      <td>{tx.description || '-'}</td>
                    </tr>
                  ))}
                </tbody>
              </table>
            </div>
          </div>
        )}

        {/* New Wallet Tile */}
        {showNewWallet && (
          <div className="tile new-wallet-tile">
            <div className="tile-header">
              <h3>Create New Wallet</h3>
              <button className="close-btn" onClick={() => setShowNewWallet(false)}>✕</button>
            </div>
            <form onSubmit={handleCreateWallet} className="wallet-form">
              <label>Select Currency:</label>
              <select value={newCurrency} onChange={(e) => setNewCurrency(e.target.value)}>
                <option value="MYR">🇲🇾 Malaysian Ringgit (MYR)</option>
                <option value="SGD">🇸🇬 Singapore Dollar (SGD)</option>
                <option value="USD">🇺🇸 US Dollar (USD)</option>
              </select>
              <button type="submit" className="create-wallet-btn">Create Wallet</button>
            </form>
          </div>
        )}

        {/* Image Upload Tile */}
        {showImageUpload && (
          <div className="tile upload-tile">
            <div className="tile-header">
              <h3>Upload Profile Image</h3>
              <button className="close-btn" onClick={() => setShowImageUpload(false)}>✕</button>
            </div>
            <div className="upload-area">
              <input 
                type="file" 
                accept="image/*"
                onChange={handleImageUpload}
                id="file-input"
                style={{ display: 'none' }}
              />
              <label htmlFor="file-input" className="upload-label">
                <div className="upload-icon">📁</div>
                <div>Click to upload image</div>
                <div className="upload-hint">PNG, JPG, GIF up to 10MB</div>
              </label>
            </div>
          </div>
        )}
      </div>
    </div>
  );
}

export default Dashboard;