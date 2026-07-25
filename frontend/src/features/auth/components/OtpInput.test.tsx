import { render, screen } from '@testing-library/react'
import userEvent from '@testing-library/user-event'
import { describe, expect, it, vi } from 'vitest'
import { OtpInput } from './OtpInput'

describe('OtpInput', () => {
  it('accepts a pasted six-digit code', async () => { const onChange = vi.fn(); render(<OtpInput value="" onChange={onChange} />); await userEvent.click(screen.getByLabelText('Digit 1')); await userEvent.paste('123456'); expect(onChange).toHaveBeenCalledWith('123456') })
  it('exposes every cell with a distinct accessible label', () => { render(<OtpInput value="123456" onChange={() => undefined} />); expect(screen.getAllByRole('textbox')).toHaveLength(6); expect(screen.getByLabelText('Digit 6')).toHaveValue('6') })
})