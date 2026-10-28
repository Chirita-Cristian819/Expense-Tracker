import { useState } from 'react'
import type { SubmitEvent } from 'react'
import { ArrowRight, Eye, EyeOff, LockKeyhole, Mail } from 'lucide-react'
import { Button } from '@/components/ui/button'
import { Input } from '@/components/ui/input'
import { Label } from '@/components/ui/label'
import { readCsrfToken } from '@/api/csfr.js'
import type { User } from '@/types/user'

type LoginFormProps = {
  onLogin: (user: User) => void,
  onEmailNotVerified: (email: string) => void
}

function LoginForm({ onLogin, onEmailNotVerified }: LoginFormProps) {
  const [showPassword, setShowPassword] = useState(false)
  const [message, setMessage] = useState('')

  async function handleSubmit(event: SubmitEvent<HTMLFormElement>) {
    event.preventDefault()
    const { email, password } = Object.fromEntries(new FormData(event.currentTarget))

    const response = await fetch('/api/v1/auth/login', {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
        'X-XSRF-TOKEN': readCsrfToken() ?? ''
      },
      body: JSON.stringify({ email, password })
    })

    if (response.ok) {
      const meResponse = await fetch('/api/v1/auth/me')
      if (meResponse.ok) {
        onLogin(await meResponse.json())
      }
      return
    }

    const error = await response.json().catch(() => null)

    if (error?.code === 'EMAIL_NOT_VERIFIED') {
      onEmailNotVerified(String(email))
      return
    }

    setMessage(error?.message ?? 'Unable to log in at this time, please attempt later!')
  }

  return (
    <form className="w-full max-w-3xl text-emerald-950 lg:relative lg:-top-12 lg:w-[90%]" onSubmit={handleSubmit}>
      <span className="mb-4 inline-flex size-12 items-center justify-center rounded-2xl border border-emerald-900/10 bg-white/70">
        <LockKeyhole className="size-5" aria-hidden="true" />
      </span>
      <p className="text-xs font-medium uppercase tracking-[0.2em] text-emerald-800">Welcome back</p>
      <h2 className="mt-3 font-serif text-[clamp(2.25rem,2.5vw,3.5rem)] leading-tight tracking-tight">Log In</h2>
      <p className="mt-3 text-sm leading-6 text-stone-500">Enter your email and password to continue.</p>

      <div className="mt-6 space-y-4">
        <div className="space-y-2.5">
          <Label htmlFor="email" className="text-sm font-medium">Email address</Label>
          <div className="relative">
            <Mail aria-hidden="true" className="pointer-events-none absolute left-3.5 top-1/2 size-4 -translate-y-1/2 text-stone-400" />
            <Input id="email" name="email" type="email" autoComplete="username" required placeholder="you@example.com" className="h-12 rounded-lg border-stone-300 bg-white/80 pl-10 shadow-none focus-visible:border-emerald-700 focus-visible:ring-emerald-700/15" />
          </div>
        </div>

        <div className="space-y-2.5">
          <Label htmlFor="password" className="text-sm font-medium">Password</Label>
          <div className="relative">
            <LockKeyhole aria-hidden="true" className="pointer-events-none absolute left-3.5 top-1/2 size-4 -translate-y-1/2 text-stone-400" />
            <Input id="password" name="password" type={showPassword ? 'text' : 'password'} autoComplete="current-password" required placeholder="Enter your password" className="h-12 rounded-lg border-stone-300 bg-white/80 pl-10 pr-12 shadow-none focus-visible:border-emerald-700 focus-visible:ring-emerald-700/15" />
            <button type="button" aria-label={showPassword ? 'Hide password' : 'Show password'} aria-controls="password" aria-pressed={showPassword} onClick={() => setShowPassword(!showPassword)} className="absolute right-1 top-1 flex size-10 items-center justify-center rounded-md text-stone-400 hover:text-emerald-800 focus-visible:outline-2 focus-visible:outline-emerald-700">
              {showPassword ? <EyeOff className="size-4" aria-hidden="true" /> : <Eye className="size-4" aria-hidden="true" />}
            </button>
          </div>
        </div>

        <Button type="submit" className="h-12 w-full cursor-pointer rounded-lg bg-[#103f32] text-sm text-white hover:bg-emerald-900 focus-visible:ring-emerald-700/30">
          Log in<ArrowRight className="size-4" aria-hidden="true" />
        </Button>
      </div>

      <p role="status" className="mt-4 text-sm text-stone-600">{message}</p>
      <p className="mt-6 text-center text-sm text-stone-500">
        New here?{' '}
        <button type="button" disabled title="Registration page is not connected yet" className="font-medium text-emerald-900 underline underline-offset-4 disabled:cursor-not-allowed">
          Create an account
        </button>
      </p>

    </form>
  )
}

export default LoginForm
