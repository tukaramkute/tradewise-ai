import { render, screen } from '@testing-library/react'
import { describe, expect, it } from 'vitest'
import { Button } from './Button'

describe('Button', () => { it('disables interaction while loading', () => { render(<Button loading>Save changes</Button>); expect(screen.getByRole('button', { name: /save changes/i })).toBeDisabled() }) })