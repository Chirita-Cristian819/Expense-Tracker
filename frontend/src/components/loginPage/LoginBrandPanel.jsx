import { ArrowDownLeft, ArrowUpRight, ChartNoAxesColumnIncreasing, Leaf, ShoppingBasket, Wallet } from 'lucide-react'

function LoginBrandPanel() {
  return (
    <section className="relative isolate hidden overflow-hidden bg-[#103f32] p-12 text-[#faf8f2] lg:flex lg:flex-col xl:p-16">
      <div aria-hidden="true" className="absolute -right-40 -top-40 -z-10 size-[550px] rounded-full border border-white/10 bg-white/[0.025]" />
      <div aria-hidden="true" className="absolute -bottom-56 -left-48 -z-10 size-[650px] rounded-full border border-white/10 bg-emerald-300/[0.035]" />

      <div className="flex items-center gap-3 text-xl font-semibold tracking-tight">
        <span className="flex size-10 items-center justify-center rounded-xl bg-white/10"><Leaf className="size-5" aria-hidden="true" /></span>
        Pocket Tracer
      </div>

      <div className="my-auto py-16">
        <p className="mb-5 text-xs font-medium uppercase tracking-[0.24em] text-emerald-200/70">Less guesswork. More clarity.</p>
        <h1 className="max-w-full font-serif text-[clamp(2.75rem,3.5vw,5rem)] leading-[1.08] tracking-tight">
          It all adds up.<br />Eventually.
        </h1>
        <p className="mt-6 max-w-[80%] text-base leading-7 text-emerald-50/70">
          Keep your spending in sight, one day at a time.
        </p>

        <div aria-hidden="true" className="relative mx-auto mt-12 w-[80%] max-w-2xl pb-8">
          <div className="relative rotate-[-3deg] rounded-2xl border border-white/60 bg-[#faf8f2] p-6 text-emerald-950 shadow-xl">
            <div className="flex items-center justify-between text-sm text-stone-500">
              <span>This month's spending</span><Wallet className="size-5 text-emerald-800" />
            </div>
            <div className="mt-3 flex items-end justify-between gap-4">
              <p className="text-3xl font-semibold tracking-tight">2,450 <span className="text-sm font-normal text-stone-500">RON</span></p>
              <div className="flex h-12 items-end gap-1.5">
                {[40, 65, 48, 80, 60, 95].map((height, index) => (
                  <span key={index} className="w-3 rounded-t-sm bg-emerald-800/70" style={{ height: `${height}%` }} />
                ))}
              </div>
            </div>
            <p className="mt-4 flex items-center gap-1 text-xs text-emerald-700"><ArrowDownLeft className="size-3.5" />12% less than last month</p>
          </div>
          <div className="relative ml-10 mt-4 flex rotate-[3deg] items-center gap-4 rounded-2xl border border-white/60 bg-[#faf8f2] p-5 text-emerald-950 shadow-xl">
            <span className="flex size-11 shrink-0 items-center justify-center rounded-xl bg-emerald-100"><ShoppingBasket className="size-5" /></span>
            <div className="flex-1"><p className="text-xs text-stone-500">Groceries</p><p className="mt-1 font-semibold">620 RON</p></div>
            <span className="rounded-full bg-emerald-100 px-2.5 py-1 text-xs text-emerald-800">On track</span>
          </div>
        </div>
      </div>

      <div className="flex flex-wrap gap-7 border-t border-white/15 pt-6 text-xs text-emerald-50/75">
        <span className="flex items-center gap-2"><Wallet className="size-4" aria-hidden="true" />Track your spending</span>
        <span className="flex items-center gap-2"><ChartNoAxesColumnIncreasing className="size-4" aria-hidden="true" />See the bigger picture</span>
        <span className="flex items-center gap-2"><ArrowUpRight className="size-4" aria-hidden="true" />Build better habits</span>
      </div>
    </section>
  )
}

export default LoginBrandPanel
