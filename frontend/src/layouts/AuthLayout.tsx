import { Activity, BarChart3, BrainCircuit, TrendingUp } from 'lucide-react'
import { motion } from 'framer-motion'
import { Link, Outlet } from 'react-router-dom'
import { BrandMark } from '../components/ui/DataDisplay'

export function AuthLayout() {
  return <main className="grid min-h-screen bg-ink-950 lg:grid-cols-[minmax(0,1.04fr)_minmax(440px,.96fr)]">
    <section className="relative hidden overflow-hidden border-r border-white/[.07] lg:flex lg:flex-col lg:justify-between lg:p-10 xl:p-14">
      <div className="grid-surface absolute inset-0 opacity-70" /><Link to="/" className="relative z-10 w-fit focus-ring rounded-md"><BrandMark /></Link>
      <motion.div className="relative z-10 max-w-xl" initial={{ opacity: 0, y: 20 }} animate={{ opacity: 1, y: 0 }}>
        <span className="mb-5 inline-flex items-center gap-2 rounded-full border border-mint-400/20 bg-mint-400/[.07] px-3 py-1.5 text-xs font-bold text-mint-400"><Activity className="size-3.5" />MARKET INTELLIGENCE, REFINED</span>
        <h1 className="font-display text-5xl font-bold leading-[1.08] xl:text-6xl">Trade with clarity.<br /><span className="text-mint-400">Learn from every move.</span></h1>
        <p className="mt-5 max-w-lg text-base leading-7 text-slate-400">A private command center for portfolios, trading journals, risk monitoring, and AI-guided decision review.</p>
        <div className="mt-10 grid grid-cols-3 gap-3"><Metric icon={TrendingUp} value="+18.6%" label="Portfolio alpha" /><Metric icon={BarChart3} value="2.41" label="Profit factor" /><Metric icon={BrainCircuit} value="84" label="Discipline score" /></div>
      </motion.div>
      <p className="relative z-10 text-xs text-slate-600">Built for deliberate traders, not impulsive clicks.</p>
    </section>
    <section className="flex min-h-screen items-center justify-center px-5 py-10 sm:px-8"><div className="w-full max-w-xl"><Link to="/" className="mb-10 inline-flex lg:hidden"><BrandMark /></Link><Outlet /></div></section>
  </main>
}
function Metric({ icon: Icon, value, label }: { icon: typeof Activity; value: string; label: string }) { return <div className="glass rounded-lg p-4"><Icon className="mb-6 size-4 text-mint-400" /><strong className="block font-display text-xl">{value}</strong><span className="text-xs text-slate-500">{label}</span></div> }