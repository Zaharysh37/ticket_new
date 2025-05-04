import React, { useState, useEffect } from 'react';
import {Table, Button, Modal, Form, Input, message, Spin, Popconfirm, Empty, Space} from 'antd';
import { PlusOutlined, SearchOutlined, EditOutlined, DeleteOutlined, ClearOutlined } from '@ant-design/icons';
import {
    getPatients,
    getPatientById,
    createPatient,
    updatePatient,
    deletePatient,
    findPatientsByFilter
} from '../services/api';

const PatientsPage = () => {
    const [patients, setPatients] = useState([]);
    const [isModalVisible, setIsModalVisible] = useState(false);
    const [editingPatientId, setEditingPatientId] = useState(null);
    const [loading, setLoading] = useState(true);
    const [searchForm] = Form.useForm();
    const [form] = Form.useForm();
    const [filters, setFilters] = useState({
        name: null,
        phoneNumber: null
    });

    useEffect(() => {
        fetchPatients();
    }, []);

    const fetchPatients = async () => {
        try {
            setLoading(true);
            const data = await getPatients();
            setPatients(data || []);
        } catch (error) {
            message.error('Ошибка загрузки пациентов');
        } finally {
            setLoading(false);
        }
    };

    const handleSearch = async (values) => {
        try {
            setLoading(true);
            const searchParams = {
                name: values.name?.trim() || undefined,
                phoneNumber: values.phone?.trim() || undefined
            };

            const data = await findPatientsByFilter(searchParams.name, searchParams.phoneNumber);
            setPatients(data || []);
        } catch (error) {
            if (error.response?.status === 404) {
                setPatients([]);
            } else {
                message.error('Ошибка поиска пациентов');
            }
        } finally {
            setLoading(false);
        }
    };

    const resetSearch = () => {
        searchForm.resetFields();
        fetchPatients();
    };

    const handleTableChange = (pagination, filters) => {
        setFilters({
            name: filters.name || null,
            phoneNumber: filters.phoneNumber || null
        });
    };

    const resetAllFilters = () => {
        setFilters({
            name: null,
            phoneNumber: null
        });
    };

    const checkPatientExists = (name, phoneNumber, excludeId = null) => {
        return patients.some(p =>
            p.name === name &&
            p.phoneNumber === phoneNumber &&
            p.id !== excludeId
        );
    };

    const handleCreate = async () => {
        try {
            const values = await form.validateFields();
            setLoading(true);

            if (checkPatientExists(values.name, values.phoneNumber)) {
                message.error('Пациент с такими данными уже существует');
                return;
            }

            await createPatient(values);
            message.success('Пациент успешно создан');
            resetModal();
            await fetchPatients();
        } catch (error) {
            if (error.response?.status === 409) {
                const serverMessage = error.response.data?.error ||
                    'Пациент с такими данными уже существует';
                message.error(serverMessage);
            } else {
                message.error('Ошибка при создании пациента');
            }
            console.error('Create error:', error);
        } finally {
            setLoading(false);
        }
    };

    const handleUpdate = async () => {
        try {
            const values = await form.validateFields();
            setLoading(true);

            if (checkPatientExists(values.name, values.phoneNumber, editingPatientId)) {
                message.error('Пациент с такими данными уже существует');
                return;
            }

            await updatePatient(editingPatientId, values);
            message.success('Данные пациента обновлены');
            resetModal();
            await fetchPatients();
        } catch (error) {
            if (error.response?.status === 409) {
                const serverMessage = error.response.data?.error ||
                    'Пациент с такими данными уже существует';
                message.error(serverMessage);
            } else {
                message.error('Ошибка при обновлении пациента');
            }
            console.error('Update error:', error);
        } finally {
            setLoading(false);
        }
    };

    const handleDelete = async (id) => {
        try {
            await deletePatient(id);
            message.success('Пациент удален');
            await fetchPatients();
        } catch (error) {
            message.error('Ошибка при удалении пациента');
        }
    };

    const handleEdit = async (id) => {
        try {
            setLoading(true);
            const patient = await getPatientById(id);
            form.setFieldsValue({
                name: patient.name,
                phoneNumber: patient.phoneNumber
            });
            setEditingPatientId(id);
            setIsModalVisible(true);
        } catch (error) {
            message.error('Ошибка загрузки данных пациента');
        } finally {
            setLoading(false);
        }
    };

    const resetModal = () => {
        form.resetFields();
        setEditingPatientId(null);
        setIsModalVisible(false);
    };

    const columns = [
        {
            title: 'ФИО',
            dataIndex: 'name',
            key: 'name',
            width: 200,
            filters: [...new Set(patients.map(p => p.name))].map(name => ({
                text: name,
                value: name,
            })),
            onFilter: (value, record) => record.name.includes(value),
            filterSearchPlaceholder: 'Поиск',
            filteredValue: filters.name || null,
        },
        {
            title: 'Телефон',
            dataIndex: 'phoneNumber',
            key: 'phoneNumber',
            width: 150,
            filters: [...new Set(patients.map(p => p.phoneNumber))].map(phone => ({
                text: phone,
                value: phone,
            })),
            onFilter: (value, record) => record.phoneNumber.includes(value),
            filterSearchPlaceholder: 'Поиск',
            filteredValue: filters.phoneNumber || null,
        },
        {
            title: 'Действия',
            key: 'actions',
            fixed: 'right',
            width: 55,
            align: 'center',
            render: (_, record) => (
                <div style={{
                    display: 'flex',
                    justifyContent: 'space-evenly',
                    width: '100%'
                }}>
                    <Button
                        type="text"
                        icon={<EditOutlined />}
                        onClick={() => handleEdit(record.id)}
                        style={{
                            color: '#1890ff',
                            minWidth: 24,
                            padding: 0
                        }}
                    />
                    <Popconfirm
                        title="Удалить пациента?"
                        onConfirm={() => handleDelete(record.id)}
                        okText="Да"
                        cancelText="Нет"
                    >
                        <Button
                            type="text"
                            icon={<DeleteOutlined />}
                            style={{
                                color: '#ff4d4f',
                                minWidth: 24,
                                padding: 0
                            }}
                        />
                    </Popconfirm>
                </div>
            ),
        },
    ];

    return (
        <Spin spinning={loading}>
            <div style={{
                marginBottom: 16,
                background: '#fff',
                padding: 16,
                borderRadius: 8
            }}>
                <Form
                    layout="inline"
                    form={searchForm}
                    onFinish={handleSearch}
                    style={{ marginBottom: 0 }}
                >
                    <Form.Item
                        name="name"
                        label="ФИО"
                        style={{ marginBottom: 16, marginRight: 16 }}
                    >
                        <Input
                            placeholder="Поиск по ФИО"
                            allowClear
                            style={{ width: 180 }}
                        />
                    </Form.Item>

                    <Form.Item
                        name="phone"
                        label="Телефон"
                        style={{ marginBottom: 16, marginRight: 16 }}
                    >
                        <Input
                            placeholder="Поиск по телефону"
                            allowClear
                            style={{ width: 180 }}
                        />
                    </Form.Item>

                    <Form.Item style={{ marginBottom: 16 }}>
                        <Button type="primary" htmlType="submit" icon={<SearchOutlined />}>
                            Поиск
                        </Button>
                        <Button
                            style={{ marginLeft: 16 }}
                            onClick={resetSearch}
                        >
                            Сброс
                        </Button>
                    </Form.Item>

                    <Form.Item style={{ marginBottom: 16, marginLeft: 'auto' }}>
                        <Space>
                            <Button
                                icon={<ClearOutlined />}
                                onClick={resetAllFilters}
                                disabled={!filters.name && !filters.phoneNumber}
                            >
                                Сбросить фильтры
                            </Button>
                            <Button
                                type="primary"
                                icon={<PlusOutlined />}
                                onClick={() => setIsModalVisible(true)}
                            >
                                Добавить пациента
                            </Button>
                        </Space>
                    </Form.Item>
                </Form>
            </div>

            <div style={{
                background: '#fff',
                padding: 16,
                borderRadius: 8
            }}>
                <Table
                    columns={columns}
                    dataSource={patients}
                    rowKey="id"
                    loading={loading}
                    scroll={{ x: 800 }}
                    pagination={{
                        pageSize: 10,
                        showSizeChanger: false
                    }}
                    bordered
                    locale={{
                        filterReset: 'Сбросить',
                        filterConfirm: 'ОК',
                        emptyText: <Empty image={Empty.PRESENTED_IMAGE_SIMPLE} description="Нет данных" />
                    }}
                    onChange={handleTableChange}
                />
            </div>

            <Modal
                title={editingPatientId ? 'Редактирование пациента' : 'Новый пациент'}
                open={isModalVisible}
                onOk={editingPatientId ? handleUpdate : handleCreate}
                onCancel={resetModal}
                okText={editingPatientId ? 'Обновить' : 'Создать'}
                cancelText="Отмена"
                confirmLoading={loading}
                maskClosable={false}
                destroyOnClose
                forceRender
            >
                <Form form={form} layout="vertical">
                    <Form.Item
                        name="name"
                        label="ФИО пациента"
                        rules={[
                            { required: true, message: 'Пожалуйста, введите ФИО пациента!' },
                            { min: 3, message: 'ФИО должно содержать не менее 3 символов' }
                        ]}
                    >
                        <Input placeholder="Введите ФИО пациента" />
                    </Form.Item>

                    <Form.Item
                        name="phoneNumber"
                        label="Номер телефона"
                        rules={[
                            { required: true, message: 'Пожалуйста, введите номер телефона!' },
                            {
                                pattern: /^(802[5-9]\d{7})$/,
                                message: 'Телефон должен быть в формате 802(5-9)xxxхxxx'
                            }
                        ]}
                    >
                        <Input placeholder="Введите номер (802(5-9)xxxхxxx)" />
                    </Form.Item>
                </Form>
            </Modal>
        </Spin>
    );
};

export default PatientsPage;