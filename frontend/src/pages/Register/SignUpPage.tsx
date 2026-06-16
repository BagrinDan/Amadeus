// pages/SignUp/SignUpPage.tsx
import { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import styles from './SignUpPage.module.css';
import { signUpApi } from '../../api/auth/signUpApi';
import { type SignUpRequest } from '../../types/auth/SignUpRequest';



export const SignUpPage = () => {
  const [username, setUsername] = useState('');
  const [password, setPassword] = useState('');
  const [confirmPassword, setConfirmPassword] = useState('');
  const [error, setError] = useState('');
  const [success, setSuccess] = useState('');
  const navigate = useNavigate();

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    setError('');
    setSuccess('');

    if (!username.trim() || !password || !confirmPassword) {
      setError('CRITICAL ERROR: All fields are required.');
      return;
    }

    if (password !== confirmPassword) {
      setError('CRITICAL ERROR: Access codes do not match.');
      return;
    }

    try {
        const requestData: SignUpRequest = { username, password, confirmPassword };
        const text = await signUpApi(requestData);
        setSuccess(`REGISTRATION COMPLETE: ${text}`);
        setTimeout(() => navigate('/login'), 2000);
    } catch (err: any) {
        setError(`ACCESS DENIED: ${err.message}`);
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
        onClick={() => navigate('/login')}
      >
        🖲️ [Abort Registration]
      </button>

      <main className={styles.loginCard}>
        <div className={styles.cardHeader}>
          <span className={styles.blinkToken}>⚠</span> NEW MEMBER REGISTRATION
        </div>

        <div className={styles.cardBody}>
          <h2 className={styles.title}>[ ENLIST AS LAB MEMBER ]</h2>
          <p className={styles.subtitle}>
            Submit credentials to the Amadeus mainframe for identity registration.
          </p>

          {error && <div className={styles.errorBox}>{error}</div>}
          {success && <div className={styles.successBox}>{success}</div>}

          <form onSubmit={handleSubmit} className={styles.form}>
            <div className={styles.inputGroup}>
              <label className={styles.label}>LAB MEMBER LOGIN</label>
              <input
                className={styles.inputField}
                type="text"
                value={username}
                onChange={(e) => setUsername(e.target.value)}
                autoComplete="off"
              />
            </div>

            <div className={styles.inputGroup}>
              <label className={styles.label}>ACCESS CODE</label>
              <input
                className={styles.inputField}
                type="password"
                value={password}
                onChange={(e) => setPassword(e.target.value)}
              />
            </div>

            <div className={styles.inputGroup}>
              <label className={styles.label}>CONFIRM ACCESS CODE</label>
              <input
                className={styles.inputField}
                type="password"
                value={confirmPassword}
                onChange={(e) => setConfirmPassword(e.target.value)}
              />
            </div>

            <button type="submit" className={styles.submitButton}>
              Transmit Registration
            </button>
            <button 
            className={styles.submitButton}
              onClick={() => navigate('/login')}
            >              
            Login
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