import { useState } from 'react'
import { ArrowLeft, CheckCircle2, LoaderCircle, Mail, Send } from 'lucide-react'
import LoginBrandPanel from '@/components/loginPage/LoginBrandPanel.jsx'
import { Button } from '@/components/ui/button'
import { readCsrfToken } from '@/api/csfr.js'

type VerifyEmailPageProps = {
  email: string
  onBack: () => void
}

function VerifyEmailPage({ email, onBack }: VerifyEmailPageProps) {
  const [isSending, setIsSending] = useState(false)
  const [isSent, setIsSent] = useState(false)
  const [error, setError] = useState('')

  async function handleSendVerificationEmail() {
    if (isSending) return

    setIsSending(true)
    setError('')
    setIsSent(false)

    try {
      const response = await fetch('/api/v1/auth/resend-verification', {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
          'X-XSRF-TOKEN': readCsrfToken() ?? '',
        },
        body: JSON.stringify({ email }),
      })

      if (!response.ok) {
        setError('Unable to send the verification email. Please try again later.')
        return
      }

      setIsSent(true)
    } catch {
      setError('Unable to reach the server. Check your connection and try again.')
    } finally {
      setIsSending(false)
    }
  }

  return (
    <main className="grid min-h-svh bg-[#faf8f2] lg:grid-cols-2">
      <LoginBrandPanel />
      <section aria-labelledby="verify-email-title" className="flex min-w-0 flex-col px-6 py-8 sm:px-10 lg:px-[5%]">
        <div className="mb-8 text-sm font-semibold text-emerald-950 lg:hidden">Pocket Tracer</div>
        <div className="flex flex-1 items-center justify-center py-8 sm:py-12">
          <div className="w-full max-w-3xl text-emerald-950 lg:relative lg:-top-12 lg:w-[90%]">
            <span className="mb-4 inline-flex size-12 items-center justify-center rounded-2xl border border-emerald-900/10 bg-white/70">
              <Mail className="size-5" aria-hidden="true" />
            </span>
            <p className="text-xs font-medium uppercase tracking-[0.2em] text-emerald-800">One more step</p>
            <h1 id="verify-email-title" className="mt-3 font-serif text-[clamp(2.25rem,2.5vw,3.5rem)] leading-tight tracking-tight">Verify your email</h1>
            <p className="mt-3 text-sm leading-6 text-stone-500">Send a verification link to your email address before logging in.</p>

            <div className="mt-6 flex items-center gap-3 rounded-lg border border-stone-300 bg-white/80 p-4">
              <Mail className="size-4 shrink-0 text-stone-400" aria-hidden="true" />
              <span className="min-w-0 break-all text-sm font-medium">{email}</span>
            </div>

            <Button type="button" onClick={handleSendVerificationEmail} disabled={isSending || !email.trim()} className="mt-6 h-12 w-full cursor-pointer rounded-lg bg-[#103f32] text-sm text-white hover:bg-emerald-900 focus-visible:ring-emerald-700/30">
              {isSending ? <LoaderCircle className="size-4 animate-spin" aria-hidden="true" /> : <Send className="size-4" aria-hidden="true" />}
              {isSending ? 'Sending...' : isSent ? 'Send again' : 'Send verification email'}
            </Button>

            <div role="status" aria-live="polite">
              {isSent && (
                <div className="mt-4 flex items-start gap-2 rounded-lg border border-emerald-200 bg-emerald-50 p-4 text-sm leading-6 text-emerald-900">
                  <CheckCircle2 className="mt-1 size-4 shrink-0" aria-hidden="true" />
                  <p>Check your inbox and spam folder for the verification link. After verifying, return to log in.</p>
                </div>
              )}
            </div>
            {error && <p role="alert" className="mt-4 text-sm leading-6 text-red-700">{error}</p>}

            <button type="button" onClick={onBack} className="mx-auto mt-6 flex items-center gap-2 rounded-sm text-sm font-medium text-emerald-900 underline-offset-4 hover:underline focus-visible:outline-2 focus-visible:outline-offset-4 focus-visible:outline-emerald-700">
              <ArrowLeft className="size-4" aria-hidden="true" />Back to login
            </button>
          </div>
        </div>
      </section>
    </main>
  )
}

export default VerifyEmailPage
