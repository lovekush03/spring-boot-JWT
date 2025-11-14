ChatGpt Chat Link used to creating application : https://chatgpt.com/share/6916cc2a-a5cc-8002-8ba7-5f005a071342
Spring Security Flow Chart
┌───────────────────────────────────────────┐
│ AuthenticationManager (ProviderManager)    │
└───────────────────────────────────────────┘
                     │
                     ▼
┌───────────────────────────────────────────┐
│ DaoAuthenticationProvider                  │
│ - Calls UserDetailsService                 │
│ - Uses PasswordEncoder                     │
└───────────────────────────────────────────┘
                     │
                     ▼
┌───────────────────────────────────────────┐
│ UserDetailsService                         │
│ - Loads UserDetails (username, password, roles) │
└───────────────────────────────────────────┘
                     │
                     ▼
┌───────────────────────────────────────────┐
│ PasswordEncoder.matches()                 │
└───────────────────────────────────────────┘
                     │
                     ▼
     ✅ SUCCESS → authenticated token returned
     ❌ FAILURE → AuthenticationException (401)

