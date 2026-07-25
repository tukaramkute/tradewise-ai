import { z } from 'zod'

export const passwordSchema = z.string().min(8, 'Use at least 8 characters').regex(/[a-z]/, 'Add a lowercase letter').regex(/[A-Z]/, 'Add an uppercase letter').regex(/\d/, 'Add a number').regex(/[^A-Za-z0-9]/, 'Add a special character')
const phone = z.string().regex(/^\+?[1-9]\d{6,14}$/, 'Use an international phone number')
export const loginSchema = z.object({ identifier: z.string().min(1, 'Email or username is required'), password: z.string().min(1, 'Password is required'), remember: z.boolean() })
export const registerSchema = z.object({
  firstName: z.string().min(1, 'First name is required').max(50), lastName: z.string().min(1, 'Last name is required').max(50),
  username: z.string().regex(/^[A-Za-z0-9._-]{3,30}$/, 'Use 3-30 letters, numbers, dots, underscores, or hyphens'),
  email: z.email(), phoneNumber: phone, password: passwordSchema, confirmPassword: z.string(),
  country: z.string().min(1), timeZone: z.string().min(1), preferredCurrency: z.string().length(3),
  profileImageUrl: z.string().optional(), terms: z.boolean().refine(Boolean, 'Accept the terms to continue'),
}).refine((data) => data.password === data.confirmPassword, { path: ['confirmPassword'], message: 'Passwords do not match' })
export const emailSchema = z.object({ email: z.email() })
export const resetSchema = z.object({ email: z.email(), otp: z.string().length(6, 'Enter the 6-digit code'), newPassword: passwordSchema, confirmPassword: z.string() }).refine((data) => data.newPassword === data.confirmPassword, { path: ['confirmPassword'], message: 'Passwords do not match' })
export const changePasswordSchema = z.object({ oldPassword: z.string().min(1, 'Current password is required'), newPassword: passwordSchema, confirmPassword: z.string() }).refine((data) => data.newPassword === data.confirmPassword, { path: ['confirmPassword'], message: 'Passwords do not match' })
export const updateProfileSchema = z.object({ firstName: z.string().min(1).max(50), lastName: z.string().min(1).max(50), phoneNumber: phone, country: z.string().min(1), timeZone: z.string().min(1), preferredCurrency: z.string().length(3), profileImageUrl: z.string().optional() })
export type LoginValues = z.infer<typeof loginSchema>
export type RegisterValues = z.infer<typeof registerSchema>