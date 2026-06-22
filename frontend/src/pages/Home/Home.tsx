import { useEffect, useState } from 'react';
import { useNavigate } from 'react-router-dom';
import styles from './Home.module.css';
import { getVersion } from "../../api/global/version";

export const HomePage = () => {
  const [message, setMessage] = useState('');
  const navigate = useNavigate();
  const [version, setVersion] = useState("");

  useEffect(() => {
    getVersion().then(setVersion);
  }, []);

  return (
    <div className={styles.container}>
      <div className={styles.loginWrapper}>
        <p> {version} </p>
        <button 
          className={styles.loginButton}
          onClick={() => navigate('/login')} 
        >
          Login as Lab Member
        </button>
      </div>

      <header className={styles.header}>
        <div className={styles.divergenceMeter}>1.048596%</div>
        <div className={styles.worldLine}>WORLD LINE DIVERGENCE METRIC</div>
      </header>
      <main className={styles.terminalPanel}>
        <div className={styles.terminalOutput}>
          <p className={styles.systemMessage}>[SYSTEM]: Amadeus ready...</p>
        </div>
        <div className={styles.inputRow}>
          <input
            className={styles.terminalInput}
            type="text"
            placeholder="Transmit cognitive data to Amadeus..."
            value={message}
            onChange={(e) => setMessage(e.target.value)}
          />
          <button className={styles.sendButton}>Send</button>
        </div>
      </main>
    </div>
  );
};