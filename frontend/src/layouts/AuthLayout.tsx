import { Activity, BarChart3, BrainCircuit, TrendingUp } from 'lucide-react'
import { motion } from 'framer-motion'
import { Link, Outlet, useLocation } from 'react-router-dom'
import { BrandMark } from '../components/ui/DataDisplay'

export function AuthLayout() {
  const isRegister = useLocation().pathname === '/register'
  return <main className={`app-canvas grid min-h-svh lg:h-svh lg:overflow-hidden ${isRegister ? 'lg:grid-cols-[minmax(400px,.82fr)_minmax(620px,1.18fr)]' : 'lg:grid-cols-[minmax(0,1.04fr)_minmax(440px,.96fr)]'}`}>
    <section className="relative hidden overflow-hidden border-r border-white/[.07] lg:flex lg:flex-col lg:justify-between lg:p-8 xl:p-12">
      <div className="grid-surface absolute inset-0 opacity-70" /><Link to="/" className="relative z-10 w-fit focus-ring rounded-md"><BrandMark /></Link>
      <motion.div className="relative z-10 max-w-xl" initial={{ opacity: 0, y: 18 }} animate={{ opacity: 1, y: 0 }} transition={{ duration: .45, ease: 'easeOut' }}>
        <span className="mb-4 inline-flex items-center gap-2 rounded-full border border-mint-400/20 bg-mint-400/[.07] px-3 py-1.5 text-xs font-bold text-mint-400"><Activity className="size-3.5" />MARKET INTELLIGENCE, REFINED</span>
        <h1 className="font-display text-4xl font-bold leading-[1.08] xl:text-5xl">Trade with clarity.<br /><span className="text-mint-400">Learn from every move.</span></h1>
        <p className="mt-4 max-w-lg text-sm leading-6 text-slate-400 xl:text-base">A private command center for portfolios, trading journals, risk monitoring, and AI-guided decision review.</p>
        <div className="mt-7 grid grid-cols-3 gap-3"><Metric icon={TrendingUp} value="+18.6%" label="Portfolio alpha" /><Metric icon={BarChart3} value="2.41" label="Profit factor" /><Metric icon={BrainCircuit} value="84" label="Discipline score" /></div>
      </motion.div>
      <p className="relative z-10 text-xs text-slate-600">Built for deliberate traders, not impulsive clicks.</p>
    </section>
    <section className={`relative flex min-h-svh justify-center px-5 sm:px-8 lg:min-h-0 lg:overflow-y-auto ${isRegister ? 'items-start py-6 lg:items-center lg:py-4' : 'items-center py-8'}`}><motion.div className={`w-full ${isRegister ? 'max-w-3xl' : 'max-w-lg'}`} initial={{ opacity: 0, x: 16 }} animate={{ opacity: 1, x: 0 }} transition={{ duration: .4 }}><Link to="/" className="mb-8 inline-flex lg:hidden"><BrandMark /></Link><Outlet /></motion.div></section>
  </main>
}
function Metric({ icon: Icon, value, label }: { icon: typeof Activity; value: string; label: string }) { return <motion.div whileHover={{ y: -3 }} className="glass rounded-lg p-4"><Icon className="mb-4 size-4 text-mint-400" /><strong className="block font-display text-xl">{value}</strong><span className="text-xs text-slate-500">{label}</span></motion.div> }