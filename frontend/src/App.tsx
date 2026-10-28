import LoginPage from '@/pages/LoginPage'
import VerifyEmailPage from '@/pages/VerifyEmailPage'
import {useEffect, useState} from "react";
import type { User } from '@/types/user';

function App() {

  const [user, setUser] = useState<User | null>(null);
  const [unverifiedEmail, setUnverifiedEmail] = useState<string | null>(null);

  useEffect(() => {
    (async () => {
      const response = await fetch('/api/v1/auth/me');
      if (response.ok) {
        setUser(await response.json());
      }
    })();
  }, []);

  if (user) {
    return <p>Welcome, {user.displayName}</p>;
  }
  if (unverifiedEmail) {
    return <VerifyEmailPage email={unverifiedEmail} onBack={() => setUnverifiedEmail(null)} />;
  }
  return <LoginPage onLogin={setUser} onEmailNotVerified={setUnverifiedEmail} />;
}

export default App