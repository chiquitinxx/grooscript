package contribution

/**
 * MrHaki Code
 * Changing * string for a closure
 */

def repeater(times) {
      { value ->
          def result = ''
          times.times { result+= value}
          return result
      }
}

assert repeater(2).call('mrhaki') == 'mrhakimrhaki'

assert repeater(2)('mrhaki') == 'mrhakimrhaki'

def repeater = { times, transformer = { it } ->
    { value ->
        def result = ''
        times.times { result+= transformer(value)}
        return result
    }
}

assert repeater(2).call('mrhaki') == 'mrhakimrhaki'
assert repeater(2)('mrhaki') == 'mrhakimrhaki'
assert repeater(2) { it.toUpperCase() } ('mrhaki') == 'MRHAKIMRHAKI'
assert repeater(2, { it.reverse() })('mrhaki') == 'ikahrmikahrm'