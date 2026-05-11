import React, { useEffect } from 'react';
import { Button, Col, FormText, Row } from 'react-bootstrap';
import { Translate, ValidatedField, ValidatedForm, isNumber, translate } from 'react-jhipster';
import { Link, useNavigate, useParams } from 'react-router';

import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { useAppDispatch, useAppSelector } from 'app/config/store';
import { getEntities as getCabinets } from 'app/entities/cabinet/cabinet.reducer';
import { getEntities as getMaterials } from 'app/entities/material/material.reducer';

import { createEntity, getEntity, reset, updateEntity } from './cabinet-part.reducer';

export const CabinetPartUpdate = () => {
  const dispatch = useAppDispatch();

  const navigate = useNavigate();

  const { id } = useParams<'id'>();
  const isNew = id === undefined;

  const materials = useAppSelector(state => state.material.entities);
  const cabinets = useAppSelector(state => state.cabinet.entities);
  const cabinetPartEntity = useAppSelector(state => state.cabinetPart.entity);
  const loading = useAppSelector(state => state.cabinetPart.loading);
  const updating = useAppSelector(state => state.cabinetPart.updating);
  const updateSuccess = useAppSelector(state => state.cabinetPart.updateSuccess);

  const handleClose = () => {
    navigate(`/cabinet-part${location.search}`);
  };

  useEffect(() => {
    if (isNew) {
      dispatch(reset());
    } else {
      dispatch(getEntity(id));
    }

    dispatch(getMaterials({}));
    dispatch(getCabinets({}));
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
    if (values.widthMm !== undefined && typeof values.widthMm !== 'number') {
      values.widthMm = Number(values.widthMm);
    }
    if (values.heightMm !== undefined && typeof values.heightMm !== 'number') {
      values.heightMm = Number(values.heightMm);
    }
    if (values.thicknessMm !== undefined && typeof values.thicknessMm !== 'number') {
      values.thicknessMm = Number(values.thicknessMm);
    }
    if (values.quantity !== undefined && typeof values.quantity !== 'number') {
      values.quantity = Number(values.quantity);
    }

    const entity = {
      ...cabinetPartEntity,
      ...values,
      material: materials.find(it => it.id.toString() === values.material?.toString()),
      cabinet: cabinets.find(it => it.id.toString() === values.cabinet?.toString()),
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
          ...cabinetPartEntity,
          material: cabinetPartEntity?.material?.id,
          cabinet: cabinetPartEntity?.cabinet?.id,
        };

  return (
    <div>
      <Row className="justify-content-center">
        <Col md="8">
          <h2 id="kalitronFurnitureStudioApp.cabinetPart.home.createOrEditLabel" data-cy="CabinetPartCreateUpdateHeading">
            <Translate contentKey="kalitronFurnitureStudioApp.cabinetPart.home.createOrEditLabel">Create or edit a CabinetPart</Translate>
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
                  id="cabinet-part-id"
                  label={translate('global.field.id')}
                  validate={{ required: true }}
                />
              )}
              <ValidatedField
                label={translate('kalitronFurnitureStudioApp.cabinetPart.partCode')}
                id="cabinet-part-partCode"
                name="partCode"
                data-cy="partCode"
                type="text"
                validate={{
                  required: { value: true, message: translate('entity.validation.required') },
                  maxLength: { value: 40, message: translate('entity.validation.maxlength', { max: 40 }) },
                }}
              />
              <ValidatedField
                label={translate('kalitronFurnitureStudioApp.cabinetPart.name')}
                id="cabinet-part-name"
                name="name"
                data-cy="name"
                type="text"
                validate={{
                  required: { value: true, message: translate('entity.validation.required') },
                  maxLength: { value: 100, message: translate('entity.validation.maxlength', { max: 100 }) },
                }}
              />
              <ValidatedField
                label={translate('kalitronFurnitureStudioApp.cabinetPart.widthMm')}
                id="cabinet-part-widthMm"
                name="widthMm"
                data-cy="widthMm"
                type="text"
                validate={{
                  required: { value: true, message: translate('entity.validation.required') },
                  validate: v => isNumber(v) || translate('entity.validation.number'),
                }}
              />
              <ValidatedField
                label={translate('kalitronFurnitureStudioApp.cabinetPart.heightMm')}
                id="cabinet-part-heightMm"
                name="heightMm"
                data-cy="heightMm"
                type="text"
                validate={{
                  required: { value: true, message: translate('entity.validation.required') },
                  validate: v => isNumber(v) || translate('entity.validation.number'),
                }}
              />
              <ValidatedField
                label={translate('kalitronFurnitureStudioApp.cabinetPart.thicknessMm')}
                id="cabinet-part-thicknessMm"
                name="thicknessMm"
                data-cy="thicknessMm"
                type="text"
                validate={{
                  required: { value: true, message: translate('entity.validation.required') },
                  validate: v => isNumber(v) || translate('entity.validation.number'),
                }}
              />
              <ValidatedField
                label={translate('kalitronFurnitureStudioApp.cabinetPart.quantity')}
                id="cabinet-part-quantity"
                name="quantity"
                data-cy="quantity"
                type="text"
                validate={{
                  required: { value: true, message: translate('entity.validation.required') },
                  min: { value: 1, message: translate('entity.validation.min', { min: 1 }) },
                  validate: v => isNumber(v) || translate('entity.validation.number'),
                }}
              />
              <ValidatedField
                label={translate('kalitronFurnitureStudioApp.cabinetPart.edgeBanding')}
                id="cabinet-part-edgeBanding"
                name="edgeBanding"
                data-cy="edgeBanding"
                type="text"
                validate={{
                  maxLength: { value: 100, message: translate('entity.validation.maxlength', { max: 100 }) },
                }}
              />
              <ValidatedField
                label={translate('kalitronFurnitureStudioApp.cabinetPart.grainDirection')}
                id="cabinet-part-grainDirection"
                name="grainDirection"
                data-cy="grainDirection"
                type="text"
                validate={{
                  maxLength: { value: 30, message: translate('entity.validation.maxlength', { max: 30 }) },
                }}
              />
              <ValidatedField
                label={translate('kalitronFurnitureStudioApp.cabinetPart.cncOperation')}
                id="cabinet-part-cncOperation"
                name="cncOperation"
                data-cy="cncOperation"
                type="text"
                validate={{
                  maxLength: { value: 120, message: translate('entity.validation.maxlength', { max: 120 }) },
                }}
              />
              <ValidatedField
                label={translate('kalitronFurnitureStudioApp.cabinetPart.notes')}
                id="cabinet-part-notes"
                name="notes"
                data-cy="notes"
                type="text"
                validate={{
                  maxLength: { value: 300, message: translate('entity.validation.maxlength', { max: 300 }) },
                }}
              />
              <ValidatedField
                id="cabinet-part-material"
                name="material"
                data-cy="material"
                label={translate('kalitronFurnitureStudioApp.cabinetPart.material')}
                type="select"
                required
              >
                <option value="" key="0" />
                {materials
                  ? materials.map(otherEntity => (
                      <option value={otherEntity.id} key={otherEntity.id}>
                        {otherEntity.name}
                      </option>
                    ))
                  : null}
              </ValidatedField>
              <FormText>
                <Translate contentKey="entity.validation.required">This field is required.</Translate>
              </FormText>
              <ValidatedField
                id="cabinet-part-cabinet"
                name="cabinet"
                data-cy="cabinet"
                label={translate('kalitronFurnitureStudioApp.cabinetPart.cabinet')}
                type="select"
                required
              >
                <option value="" key="0" />
                {cabinets
                  ? cabinets.map(otherEntity => (
                      <option value={otherEntity.id} key={otherEntity.id}>
                        {otherEntity.cabinetCode}
                      </option>
                    ))
                  : null}
              </ValidatedField>
              <FormText>
                <Translate contentKey="entity.validation.required">This field is required.</Translate>
              </FormText>
              <Button as={Link as any} id="cancel-save" data-cy="entityCreateCancelButton" to="/cabinet-part" replace variant="info">
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

export default CabinetPartUpdate;
