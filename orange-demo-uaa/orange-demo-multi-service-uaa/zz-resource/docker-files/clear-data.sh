#! /bin/bash

# 如果执行的是docker-compose down，下次再docker-compose up启动中间件时，
# kafka 可能会因为之前的强行退出而出现position错误，此时可以执行当前shell，
# 删除他们的数据。
# 我们更推荐docker-compose stop的方式停止服务，下次启动docker-compose start。

rm -rf ./data/redis/*
rm -rf ./data/elasticsearch/*
rm -rf ./data/kafka/*
rm -rf ./data/rocketmq/*
rm -rf ./data/logstash/*
rm -rf ./data/logstash/.lock
rm -rf ./data/zookeeper/*

rm -rf ./logs/redis/*
rm -rf ./logs/elasticsearch/*
rm -rf ./logs/kafka/*
rm -rf ./logs/rocketmq/*
rm -rf ./logs/logstash/*
rm -rf ./logs/zookeeper/*
