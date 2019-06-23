---
--- Created by colin.
--- DateTime: 2019-06-23 21:12
---

local key = KEYS[1]
local val = redis.call("get",key)
if val == null then
    return null
end
return val