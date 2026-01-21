local userId = ARGV[1]
local maxFlowRate = tonumber(ARGV[2]);
local currentTimeStamp = ARGV[3]
local timestampBeforeATimeWindow = ARGV[4]
local loginUserTokenValidity = ARGV[5]

-- 获取用户流量
local userTraffic = tonumber(redis.call("ZCOUNT","userTraffic",timestampBeforeATimeWindow,currentTimeStamp));
if userId=="" then
	if userTraffic >= maxFlowRate then
		return false;
	else
		redis.call("ZADD","userTraffic",currentTimeStamp,currentTimeStamp);
		return true;
	end
else
	if userTraffic > maxFlowRate then
		if redis.call("GET",userId)==nil then
			return false;
		else
			redis.call("ZADD","userTraffic",currentTimeStamp,currentTimeStamp);
			-- 如果登录用户已经持有令牌，则更新令牌的到期时间
			redis.call("SET",userId,currentTimeStamp);
			redis.call("EXPIRE",userId,loginUserTokenValidity);
			return true;
		end
	else
		redis.call("ZADD","userTraffic",currentTimeStamp,currentTimeStamp);
		redis.call("SET",userId,currentTimeStamp);
		redis.call("EXPIRE",userId,loginUserTokenValidity);
		return true;
	end
end