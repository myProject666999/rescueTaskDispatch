import React, { useEffect, useState } from 'react';
import { Row, Col, Card, Table, Tag, Statistic } from 'antd';
import ReactECharts from 'echarts-for-react';
import {
  TeamOutlined,
  AlertOutlined,
  ToolOutlined,
  UserOutlined,
  CarOutlined,
  PushpinOutlined,
} from '@ant-design/icons';
import { getDashboard } from '../services/api';
import { TASK_STATUS_MAP, AVAILABILITY_STATUS_MAP } from '../constants';

export default function Dashboard() {
  const [data, setData] = useState(null);

  const loadData = async () => {
    const res = await getDashboard();
    setData(res.data);
  };

  useEffect(() => {
    loadData();
  }, []);

  const statusPie = data?.taskStatusDistribution?.length
    ? {
        tooltip: { trigger: 'item' },
        legend: { bottom: 0 },
        series: [
          {
            type: 'pie',
            radius: ['40%', '70%'],
            avoidLabelOverlap: false,
            itemStyle: { borderRadius: 6, borderColor: '#fff', borderWidth: 2 },
            label: { show: false },
            data: data.taskStatusDistribution.map((i) => ({
              name: TASK_STATUS_MAP[i.name]?.text || i.name,
              value: i.value,
            })),
          },
        ],
      }
    : null;

  const typeBar = data?.taskTypeDistribution?.length
    ? {
        tooltip: { trigger: 'axis' },
        xAxis: {
          type: 'category',
          data: data.taskTypeDistribution.map((i) => i.name),
        },
        yAxis: { type: 'value' },
        series: [
          {
            type: 'bar',
            data: data.taskTypeDistribution.map((i) => i.value),
            itemStyle: { color: '#1890ff' },
            barWidth: 30,
          },
        ],
      }
    : null;

  const recentColumns = [
    { title: '任务编号', dataIndex: 'taskNo', width: 140 },
    { title: '任务标题', dataIndex: 'taskTitle' },
    { title: '类型', dataIndex: 'taskType', width: 100 },
    {
      title: '状态',
      dataIndex: 'status',
      width: 100,
      render: (s) => {
        const cfg = TASK_STATUS_MAP[s];
        return <Tag color={cfg?.color}>{cfg?.text}</Tag>;
      },
    },
    { title: '地点', dataIndex: 'location' },
    {
      title: '创建时间',
      dataIndex: 'createdTime',
      width: 160,
    },
  ];

  return (
    <div>
      <Row gutter={[16, 16]}>
        <Col span={4}>
          <Card>
            <Statistic
              title="任务总数"
              value={data?.taskStats?.total || 0}
              prefix={<AlertOutlined />}
              valueStyle={{ color: '#1890ff' }}
            />
          </Card>
        </Col>
        <Col span={4}>
          <Card>
            <Statistic
              title="进行中"
              value={(data?.taskStats?.dispatched || 0) + (data?.taskStats?.inProgress || 0)}
              prefix={<AlertOutlined />}
              valueStyle={{ color: '#faad14' }}
            />
          </Card>
        </Col>
        <Col span={4}>
          <Card>
            <Statistic
              title="已完成"
              value={data?.taskStats?.completed || 0}
              prefix={<AlertOutlined />}
              valueStyle={{ color: '#52c41a' }}
            />
          </Card>
        </Col>
        <Col span={4}>
          <Card>
            <Statistic
              title="队员总数"
              value={data?.rescuerStats?.total || 0}
              prefix={<TeamOutlined />}
              valueStyle={{ color: '#722ed1' }}
            />
          </Card>
        </Col>
        <Col span={4}>
          <Card>
            <Statistic
              title="路上(出动中)"
              value={data?.rescuerStats?.onTheWay || 0}
              prefix={<CarOutlined />}
              valueStyle={{ color: '#fa8c16' }}
            />
          </Card>
        </Col>
        <Col span={4}>
          <Card>
            <Statistic
              title="已到场"
              value={data?.rescuerStats?.arrived || 0}
              prefix={<PushpinOutlined />}
              valueStyle={{ color: '#52c41a' }}
            />
          </Card>
        </Col>
      </Row>

      <Row gutter={[16, 16]} style={{ marginTop: 16 }}>
        <Col span={6}>
          <Card title="队员状态分布">
            <Row gutter={[8, 8]}>
              {[
                { key: 'ON_DUTY', cfg: AVAILABILITY_STATUS_MAP.ON_DUTY, value: data?.rescuerStats?.onDuty || 0 },
                { key: 'RESTING', cfg: AVAILABILITY_STATUS_MAP.RESTING, value: data?.rescuerStats?.resting || 0 },
                { key: 'AWAY', cfg: AVAILABILITY_STATUS_MAP.AWAY, value: data?.rescuerStats?.away || 0 },
              ].map((i) => (
                <Col span={24} key={i.key}>
                  <div
                    style={{
                      padding: 12,
                      background: '#fafafa',
                      borderRadius: 6,
                      display: 'flex',
                      justifyContent: 'space-between',
                      alignItems: 'center',
                    }}
                  >
                    <div style={{ display: 'flex', alignItems: 'center', gap: 8 }}>
                      <UserOutlined />
                      <span>{i.cfg.text}</span>
                    </div>
                    <Tag color={i.cfg.color} style={{ margin: 0 }}>
                      {i.value} 人
                    </Tag>
                  </div>
                </Col>
              ))}
            </Row>
          </Card>
        </Col>

        <Col span={9}>
          <Card title="任务状态分布">{statusPie && <ReactECharts option={statusPie} style={{ height: 280 }} />}</Card>
        </Col>

        <Col span={9}>
          <Card title="任务类型分布">{typeBar && <ReactECharts option={typeBar} style={{ height: 280 }} />}</Card>
        </Col>
      </Row>

      <Card title="最近任务" style={{ marginTop: 16 }}>
        <Table
          columns={recentColumns}
          dataSource={data?.recentTasks || []}
          rowKey="id"
          pagination={false}
          size="middle"
        />
      </Card>
    </div>
  );
}
