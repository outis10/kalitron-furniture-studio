import React, { useEffect } from 'react';
import { Button, Col, Row } from 'react-bootstrap';
import { Translate, ValidatedField, ValidatedForm, translate } from 'react-jhipster';
import { Link, useNavigate, useParams } from 'react-router';

import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { useAppDispatch, useAppSelector } from 'app/config/store';
import { MaterialKind } from 'app/shared/model/enumerations/material-kind.model';

import { createEntity, getEntity, reset, updateEntity } from './material.reducer';

export const MaterialUpdate = () => {
  const dispatch = useAppDispatch();

  const navigate = useNavigate();

  const { id } = useParams<'id'>();
  const isNew = id === undefined;

  const materialEntity = useAppSelector(state => state.material.entity);
  const loading = useAppSelector(state => state.material.loading);
  const updating = useAppSelector(state => state.material.updating);
  const updateSuccess = useAppSelector(state => state.material.updateSuccess);
  const materialKindValues = Object.keys(MaterialKind);

  const handleClose = () => {
    navigate('/material');
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
    if (values.thicknessMm !== undefined && typeof values.thicknessMm !== 'number') {
      values.thicknessMm = Number(values.thicknessMm);
    }
    if (values.sheetWidthMm !== undefined && typeof values.sheetWidthMm !== 'number') {
      values.sheetWidthMm = Number(values.sheetWidthMm);
    }
    if (values.sheetHeightMm !== undefined && typeof values.sheetHeightMm !== 'number') {
      values.sheetHeightMm = Number(values.sheetHeightMm);
    }
    if (values.costPerSheetMxn !== undefined && typeof values.costPerSheetMxn !== 'number') {
      values.costPerSheetMxn = Number(values.costPerSheetMxn);
    }
    if (values.costPerSquareMeterMxn !== undefined && typeof values.costPerSquareMeterMxn !== 'number') {
      values.costPerSquareMeterMxn = Number(values.costPerSquareMeterMxn);
    }

    const entity = {
      ...materialEntity,
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
          materialKind: 'MDF',
          ...materialEntity,
        };

  return (
    <div>
      <Row className="justify-content-center">
        <Col md="8">
          <h2 id="kalitronFurnitureStudioApp.material.home.createOrEditLabel" data-cy="MaterialCreateUpdateHeading">
            <Translate contentKey="kalitronFurnitureStudioApp.material.home.createOrEditLabel">Create or edit a Material</Translate>
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
                  id="material-id"
                  label={translate('global.field.id')}
                  validate={{ required: true }}
                />
              )}
              <ValidatedField
                label={translate('kalitronFurnitureStudioApp.material.code')}
                id="material-code"
                name="code"
                data-cy="code"
                type="text"
                validate={{
                  required: { value: true, message: translate('entity.validation.required') },
                  maxLength: { value: 40, message: translate('entity.validation.maxlength', { max: 40 }) },
                }}
              />
              <ValidatedField
                label={translate('kalitronFurnitureStudioApp.material.name')}
                id="material-name"
                name="name"
                data-cy="name"
                type="text"
                validate={{
                  required: { value: true, message: translate('entity.validation.required') },
                  maxLength: { value: 120, message: translate('entity.validation.maxlength', { max: 120 }) },
                }}
              />
              <ValidatedField
                label={translate('kalitronFurnitureStudioApp.material.materialKind')}
                id="material-materialKind"
                name="materialKind"
                data-cy="materialKind"
                type="select"
              >
                {materialKindValues.map(materialKind => (
                  <option value={materialKind} key={materialKind}>
                    {translate(`kalitronFurnitureStudioApp.MaterialKind.${materialKind}`)}
                  </option>
                ))}
              </ValidatedField>
              <ValidatedField
                label={translate('kalitronFurnitureStudioApp.material.thicknessMm')}
                id="material-thicknessMm"
                name="thicknessMm"
                data-cy="thicknessMm"
                type="text"
              />
              <ValidatedField
                label={translate('kalitronFurnitureStudioApp.material.sheetWidthMm')}
                id="material-sheetWidthMm"
                name="sheetWidthMm"
                data-cy="sheetWidthMm"
                type="text"
              />
              <ValidatedField
                label={translate('kalitronFurnitureStudioApp.material.sheetHeightMm')}
                id="material-sheetHeightMm"
                name="sheetHeightMm"
                data-cy="sheetHeightMm"
                type="text"
              />
              <ValidatedField
                label={translate('kalitronFurnitureStudioApp.material.costPerSheetMxn')}
                id="material-costPerSheetMxn"
                name="costPerSheetMxn"
                data-cy="costPerSheetMxn"
                type="text"
              />
              <ValidatedField
                label={translate('kalitronFurnitureStudioApp.material.costPerSquareMeterMxn')}
                id="material-costPerSquareMeterMxn"
                name="costPerSquareMeterMxn"
                data-cy="costPerSquareMeterMxn"
                type="text"
              />
              <ValidatedField
                label={translate('kalitronFurnitureStudioApp.material.supplierName')}
                id="material-supplierName"
                name="supplierName"
                data-cy="supplierName"
                type="text"
                validate={{
                  maxLength: { value: 120, message: translate('entity.validation.maxlength', { max: 120 }) },
                }}
              />
              <ValidatedField
                label={translate('kalitronFurnitureStudioApp.material.isActive')}
                id="material-isActive"
                name="isActive"
                data-cy="isActive"
                check
                type="checkbox"
              />
              <ValidatedField
                label={translate('kalitronFurnitureStudioApp.material.notes')}
                id="material-notes"
                name="notes"
                data-cy="notes"
                type="text"
                validate={{
                  maxLength: { value: 300, message: translate('entity.validation.maxlength', { max: 300 }) },
                }}
              />
              <Button as={Link as any} id="cancel-save" data-cy="entityCreateCancelButton" to="/material" replace variant="info">
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

export default MaterialUpdate;
