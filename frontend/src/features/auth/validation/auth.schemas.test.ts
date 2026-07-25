import { describe, expect, it } from 'vitest'
import { registerSchema } from './auth.schemas'

const valid = { firstName: 'Ava', lastName: 'Stone', username: 'ava.stone', email: 'ava@example.com', phoneNumber: '+14155552671', password: 'Trade!234', confirmPassword: 'Trade!234', country: 'United States', timeZone: 'UTC', preferredCurrency: 'USD', profileImageUrl: '', terms: true }
describe('registerSchema', () => {
  it('accepts the backend registration contract', () => { expect(registerSchema.safeParse(valid).success).toBe(true) })
  it('rejects mismatched passwords and non-E.164 phone numbers', () => { const result = registerSchema.safeParse({ ...valid, phoneNumber: '555', confirmPassword: 'Different!1' }); expect(result.success).toBe(false); if (!result.success) expect(result.error.issues.map((issue) => issue.path[0])).toEqual(expect.arrayContaining(['phoneNumber', 'confirmPassword'])) })
})