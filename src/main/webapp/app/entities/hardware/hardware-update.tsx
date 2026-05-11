import React, { useEffect } from 'react';
import { Button, Col, Row } from 'react-bootstrap';
import { Translate, ValidatedField, ValidatedForm, isNumber, translate } from 'react-jhipster';
import { Link, useNavigate, useParams } from 'react-router';

import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { useAppDispatch, useAppSelector } from 'app/config/store';
import { HardwareType } from 'app/shared/model/enumerations/hardware-type.model';

import { createEntity, getEntity, reset, updateEntity } from './hardware.reducer';

export const HardwareUpdate = () => {
  const dispatch = useAppDispatch();

  const navigate = useNavigate();

  const { id } = useParams<'id'>();
  const isNew = id === undefined;

  const hardwareEntity = useAppSelector(state => state.hardware.entity);
  const loading = useAppSelector(state => state.hardware.loading);
  const updating = useAppSelector(state => state.hardware.updating);
  const updateSuccess = useAppSelector(state => state.hardware.updateSuccess);
  const hardwareTypeValues = Object.keys(HardwareType);

  const handleClose = () => {
    navigate('/hardware');
  };

  useEffect(() => {
    if (isNew) {
      dispatch(reset());
    } else {
      dispatch(getEntity(id));
    }
  }, []);

  useEffect(() => {
    if (updateSuccess) {
      handleClose();
    }
  }, [updateSuccess]);

  const saveEntity = values => {
    if (values.id !== undefined && typeof values.id !== 'number') {
      values.id = Number(values.id);
    }
    if (values.unitCostMxn !== undefined && typeof values.unitCostMxn !== 'number') {
      values.unitCostMxn = Number(values.unitCostMxn);
    }

    const entity = {
      ...hardwareEntity,
      ...values,
    };

    if (isNew) {
      dispatch(createEntity(entity));
    } else {
      dispatch(updateEntity(entity));
    }
  };

  const defaultValues = () =>
    isNew
      ? {}
      : {
          hardwareType: 'HINGE',
          ...hardwareEntity,
        };

  return (
    <div>
      <Row className="justify-content-center">
        <Col md="8">
          <h2 id="kalitronFurnitureStudioApp.hardware.home.createOrEditLabel" data-cy="HardwareCreateUpdateHeading">
            <Translate contentKey="kalitronFurnitureStudioApp.hardware.home.createOrEditLabel">Create or edit a Hardware</Translate>
          </h2>
        </Col>
      </Row>
      <Row className="justify-content-center">
        <Col md="8">
          {loading ? (
            <p>Loading...</p>
          ) : (
            <ValidatedForm defaultValues={defaultValues()} onSubmit={saveEntity}>
              {!isNew && (
                <ValidatedField
                  name="id"
                  required
                  readOnly
                  id="hardware-id"
                  label={translate('global.field.id')}
                  validate={{ required: true }}
                />
              )}
              <ValidatedField
                label={translate('kalitronFurnitureStudioApp.hardware.code')}
                id="hardware-code"
                name="code"
                data-cy="code"
                type="text"
                validate={{
                  required: { value: true, message: translate('entity.validation.required') },
                  maxLength: { value: 40, message: translate('entity.validation.maxlength', { max: 40 }) },
                }}
              />
              <ValidatedField
                label={translate('kalitronFurnitureStudioApp.hardware.name')}
                id="hardware-name"
                name="name"
                data-cy="name"
                type="text"
                validate={{
                  required: { value: true, message: translate('entity.validation.required') },
                  maxLength: { value: 120, message: translate('entity.validation.maxlength', { max: 120 }) },
                }}
              />
              <ValidatedField
                label={translate('kalitronFurnitureStudioApp.hardware.hardwareType')}
                id="hardware-hardwareType"
                name="hardwareType"
                data-cy="hardwareType"
                type="select"
              >
                {hardwareTypeValues.map(hardwareType => (
                  <option value={hardwareType} key={hardwareType}>
                    {translate(`kalitronFurnitureStudioApp.HardwareType.${hardwareType}`)}
                  </option>
                ))}
              </ValidatedField>
              <ValidatedField
                label={translate('kalitronFurnitureStudioApp.hardware.unitCostMxn')}
                id="hardware-unitCostMxn"
                name="unitCostMxn"
                data-cy="unitCostMxn"
                type="text"
                validate={{
                  required: { value: true, message: translate('entity.validation.required') },
                  validate: v => isNumber(v) || translate('entity.validation.number'),
                }}
              />
              <ValidatedField
                label={translate('kalitronFurnitureStudioApp.hardware.supplierName')}
                id="hardware-supplierName"
                name="supplierName"
                data-cy="supplierName"
                type="text"
                validate={{
                  maxLength: { value: 120, message: translate('entity.validation.maxlength', { max: 120 }) },
                }}
              />
              <ValidatedField
                label={translate('kalitronFurnitureStudioApp.hardware.isActive')}
                id="hardware-isActive"
                name="isActive"
                data-cy="isActive"
                check
                type="checkbox"
              />
              <ValidatedField
                label={translate('kalitronFurnitureStudioApp.hardware.notes')}
                id="hardware-notes"
                name="notes"
                data-cy="notes"
                type="text"
                validate={{
                  maxLength: { value: 300, message: translate('entity.validation.maxlength', { max: 300 }) },
                }}
              />
              <Button as={Link as any} id="cancel-save" data-cy="entityCreateCancelButton" to="/hardware" replace variant="info">
                <FontAwesomeIcon icon="arrow-left" />
                &nbsp;
                <span className="d-none d-md-inline">
                  <Translate contentKey="entity.action.back">Back</Translate>
                </span>
              </Button>
              &nbsp;
              <Button variant="primary" id="save-entity" data-cy="entityCreateSaveButton" type="submit" disabled={updating}>
                <FontAwesomeIcon icon="save" />
                &nbsp;
                <Translate contentKey="entity.action.save">Save</Translate>
              </Button>
            </ValidatedForm>
          )}
        </Col>
      </Row>
    </div>
  );
};

export default HardwareUpdate;
