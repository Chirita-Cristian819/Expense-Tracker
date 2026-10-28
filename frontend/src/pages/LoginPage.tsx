import { useEffect } from 'react'
import LoginBrandPanel from '@/components/loginPage/LoginBrandPanel.jsx'
import LoginForm from '@/components/loginPage/LoginForm'
import type { User } from '@/types/user'

type LoginPageProps = {
  onLogin: (user: User) => void,
  onEmailNotVerified: (email: string) => void
}

function LoginPage({ onLogin , onEmailNotVerified}: LoginPageProps) {

  useEffect(() => {
    fetch('/api/v1/auth/csrf')
  }, [])

  return (
    <main className="grid min-h-svh bg-[#faf8f2] lg:grid-cols-2">
      <LoginBrandPanel />
      <section aria-label="Sign in" className="flex min-w-0 flex-col px-6 py-8 sm:px-10 lg:px-[5%]">
        <div className="mb-8 flex items-center justify-between text-sm lg:hidden">
          <span className="font-semibold text-emerald-950 lg:hidden">Pocket Tracer</span>
        </div>
        <div className="flex flex-1 items-center justify-center py-8 sm:py-12">
          <LoginForm onLogin={onLogin} onEmailNotVerified={onEmailNotVerified} />
        </div>
      </section>
    </main>
  )
}

export default LoginPage
