import { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import styles from './Login.module.css';




export const LoginPage = () => {
  const [memberLogin, setMemberLogin] = useState('');
  const [memberPassword, setMemberPasswordCode] = useState('');
  const [error, setError] = useState('');
  
  const navigate = useNavigate();

  const handleSubmit = (e: React.FormEvent) => {
    e.preventDefault();
    setError('');

    if (memberLogin.trim() === '001' && memberPassword === 'elpsykongroo') {
      navigate('/');
    } else if (!memberLogin.trim() || !memberPassword) {
      setError('CRITICAL ERROR: Empty credentials detected.');
    } else {
      setError('ACCESS DENIED: Invalid Lab Member Login or Access Code.');
    }
  };

  /* 
    =======
      HTML
    =======
  */ 
  return (
    <div className={styles.container}>
      <button 
        className={styles.backButton}
        onClick={() => navigate('/')}
      >
        🖲️ [Abort Connection]
      </button>

      <main className={styles.loginCard}>
        <div className={styles.cardHeader}>
          <span className={styles.blinkToken}>⚠</span> SECURITY GATEWAY
        </div>

        <div className={styles.cardBody}>
          <h2 className={styles.title}>[ LABORATORY AUTHENTICATION ]</h2>
          <p className={styles.subtitle}>
            Establish encrypted uplink connection to Amadeus database mainframe.
          </p>

          {error && <div className={styles.errorBox}>{error}</div>}

          <form onSubmit={handleSubmit} className={styles.form}>
            <div className={styles.inputGroup}>
              <label className={styles.label}>LAB MEMBER LOGIN</label>
              <input
                className={styles.inputField}
                type="text"
                value={memberLogin}
                onChange={(e) => setMemberLogin(e.target.value)}
                autoComplete="off"
              />
            </div>

            <div className={styles.inputGroup}>
              <label className={styles.label}>LAB MEMBER PASSWORD</label>
              <input
                className={styles.inputField}
                type="password"
                value={memberPassword}
                onChange={(e) => setMemberPasswordCode(e.target.value)}
              />
            </div>

            <button type="submit" className={styles.submitButton}>
              Initiate Handshake
            </button>
          </form>
        </div>

        <div className={styles.cardFooter}>
          FUTURE GADGET LABORATORY MAIN POLICIES APPLY.
        </div>
      </main>
    </div>
  );
};