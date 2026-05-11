import React, { useEffect } from 'react';
import { Button, Col, FormText, Row } from 'react-bootstrap';
import { Translate, ValidatedField, ValidatedForm, isNumber, translate } from 'react-jhipster';
import { Link, useNavigate, useParams } from 'react-router';

import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { useAppDispatch, useAppSelector } from 'app/config/store';
import { getEntities as getCabinetTemplates } from 'app/entities/cabinet-template/cabinet-template.reducer';
import { getEntities as getKitchenSpecs } from 'app/entities/kitchen-spec/kitchen-spec.reducer';
import { getEntities as getMaterials } from 'app/entities/material/material.reducer';
import { CabinetCategory } from 'app/shared/model/enumerations/cabinet-category.model';
import { FinishType } from 'app/shared/model/enumerations/finish-type.model';

import { createEntity, getEntity, reset, updateEntity } from './cabinet.reducer';

export const CabinetUpdate = () => {
  const dispatch = useAppDispatch();

  const navigate = useNavigate();

  const { id } = useParams<'id'>();
  const isNew = id === undefined;

  const cabinetTemplates = useAppSelector(state => state.cabinetTemplate.entities);
  const materials = useAppSelector(state => state.material.entities);
  const kitchenSpecs = useAppSelector(state => state.kitchenSpec.entities);
  const cabinetEntity = useAppSelector(state => state.cabinet.entity);
  const loading = useAppSelector(state => state.cabinet.loading);
  const updating = useAppSelector(state => state.cabinet.updating);
  const updateSuccess = useAppSelector(state => state.cabinet.updateSuccess);
  const cabinetCategoryValues = Object.keys(CabinetCategory);
  const finishTypeValues = Object.keys(FinishType);

  const handleClose = () => {
    navigate(`/cabinet${location.search}`);
  };

  useEffect(() => {
    if (isNew) {
      dispatch(reset());
    } else {
      dispatch(getEntity(id));
    }

    dispatch(getCabinetTemplates({}));
    dispatch(getMaterials({}));
    dispatch(getKitchenSpecs({}));
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
    if (values.depthMm !== undefined && typeof values.depthMm !== 'number') {
      values.depthMm = Number(values.depthMm);
    }
    if (values.doors !== undefined && typeof values.doors !== 'number') {
      values.doors = Number(values.doors);
    }
    if (values.drawers !== undefined && typeof values.drawers !== 'number') {
      values.drawers = Number(values.drawers);
    }
    if (values.shelves !== undefined && typeof values.shelves !== 'number') {
      values.shelves = Number(values.shelves);
    }
    if (values.positionX !== undefined && typeof values.positionX !== 'number') {
      values.positionX = Number(values.positionX);
    }
    if (values.positionY !== undefined && typeof values.positionY !== 'number') {
      values.positionY = Number(values.positionY);
    }
    if (values.positionZ !== undefined && typeof values.positionZ !== 'number') {
      values.positionZ = Number(values.positionZ);
    }
    if (values.rotationDeg !== undefined && typeof values.rotationDeg !== 'number') {
      values.rotationDeg = Number(values.rotationDeg);
    }
    if (values.positionSeq !== undefined && typeof values.positionSeq !== 'number') {
      values.positionSeq = Number(values.positionSeq);
    }

    const entity = {
      ...cabinetEntity,
      ...values,
      template: cabinetTemplates.find(it => it.id.toString() === values.template?.toString()),
      material: materials.find(it => it.id.toString() === values.material?.toString()),
      spec: kitchenSpecs.find(it => it.id.toString() === values.spec?.toString()),
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
          category: 'UPPER',
          finish: 'MATTE_WHITE',
          ...cabinetEntity,
          template: cabinetEntity?.template?.id,
          material: cabinetEntity?.material?.id,
          spec: cabinetEntity?.spec?.id,
        };

  return (
    <div>
      <Row className="justify-content-center">
        <Col md="8">
          <h2 id="kalitronFurnitureStudioApp.cabinet.home.createOrEditLabel" data-cy="CabinetCreateUpdateHeading">
            <Translate contentKey="kalitronFurnitureStudioApp.cabinet.home.createOrEditLabel">Create or edit a Cabinet</Translate>
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
                  id="cabinet-id"
                  label={translate('global.field.id')}
                  validate={{ required: true }}
                />
              )}
              <ValidatedField
                label={translate('kalitronFurnitureStudioApp.cabinet.cabinetCode')}
                id="cabinet-cabinetCode"
                name="cabinetCode"
                data-cy="cabinetCode"
                type="text"
                validate={{
                  required: { value: true, message: translate('entity.validation.required') },
                  maxLength: { value: 20, message: translate('entity.validation.maxlength', { max: 20 }) },
                }}
              />
              <ValidatedField
                label={translate('kalitronFurnitureStudioApp.cabinet.category')}
                id="cabinet-category"
                name="category"
                data-cy="category"
                type="select"
              >
                {cabinetCategoryValues.map(cabinetCategory => (
                  <option value={cabinetCategory} key={cabinetCategory}>
                    {translate(`kalitronFurnitureStudioApp.CabinetCategory.${cabinetCategory}`)}
                  </option>
                ))}
              </ValidatedField>
              <ValidatedField
                label={translate('kalitronFurnitureStudioApp.cabinet.label')}
                id="cabinet-label"
                name="label"
                data-cy="label"
                type="text"
                validate={{
                  required: { value: true, message: translate('entity.validation.required') },
                  maxLength: { value: 120, message: translate('entity.validation.maxlength', { max: 120 }) },
                }}
              />
              <ValidatedField
                label={translate('kalitronFurnitureStudioApp.cabinet.widthMm')}
                id="cabinet-widthMm"
                name="widthMm"
                data-cy="widthMm"
                type="text"
                validate={{
                  required: { value: true, message: translate('entity.validation.required') },
                  validate: v => isNumber(v) || translate('entity.validation.number'),
                }}
              />
              <ValidatedField
                label={translate('kalitronFurnitureStudioApp.cabinet.heightMm')}
                id="cabinet-heightMm"
                name="heightMm"
                data-cy="heightMm"
                type="text"
                validate={{
                  required: { value: true, message: translate('entity.validation.required') },
                  validate: v => isNumber(v) || translate('entity.validation.number'),
                }}
              />
              <ValidatedField
                label={translate('kalitronFurnitureStudioApp.cabinet.depthMm')}
                id="cabinet-depthMm"
                name="depthMm"
                data-cy="depthMm"
                type="text"
                validate={{
                  required: { value: true, message: translate('entity.validation.required') },
                  validate: v => isNumber(v) || translate('entity.validation.number'),
                }}
              />
              <ValidatedField
                label={translate('kalitronFurnitureStudioApp.cabinet.doors')}
                id="cabinet-doors"
                name="doors"
                data-cy="doors"
                type="text"
                validate={{
                  required: { value: true, message: translate('entity.validation.required') },
                  min: { value: 0, message: translate('entity.validation.min', { min: 0 }) },
                  max: { value: 6, message: translate('entity.validation.max', { max: 6 }) },
                  validate: v => isNumber(v) || translate('entity.validation.number'),
                }}
              />
              <ValidatedField
                label={translate('kalitronFurnitureStudioApp.cabinet.drawers')}
                id="cabinet-drawers"
                name="drawers"
                data-cy="drawers"
                type="text"
                validate={{
                  required: { value: true, message: translate('entity.validation.required') },
                  min: { value: 0, message: translate('entity.validation.min', { min: 0 }) },
                  max: { value: 8, message: translate('entity.validation.max', { max: 8 }) },
                  validate: v => isNumber(v) || translate('entity.validation.number'),
                }}
              />
              <ValidatedField
                label={translate('kalitronFurnitureStudioApp.cabinet.shelves')}
                id="cabinet-shelves"
                name="shelves"
                data-cy="shelves"
                type="text"
                validate={{
                  min: { value: 0, message: translate('entity.validation.min', { min: 0 }) },
                  max: { value: 12, message: translate('entity.validation.max', { max: 12 }) },
                  validate: v => isNumber(v) || translate('entity.validation.number'),
                }}
              />
              <ValidatedField
                label={translate('kalitronFurnitureStudioApp.cabinet.finish')}
                id="cabinet-finish"
                name="finish"
                data-cy="finish"
                type="select"
              >
                {finishTypeValues.map(finishType => (
                  <option value={finishType} key={finishType}>
                    {translate(`kalitronFurnitureStudioApp.FinishType.${finishType}`)}
                  </option>
                ))}
              </ValidatedField>
              <ValidatedField
                label={translate('kalitronFurnitureStudioApp.cabinet.positionX')}
                id="cabinet-positionX"
                name="positionX"
                data-cy="positionX"
                type="text"
              />
              <ValidatedField
                label={translate('kalitronFurnitureStudioApp.cabinet.positionY')}
                id="cabinet-positionY"
                name="positionY"
                data-cy="positionY"
                type="text"
              />
              <ValidatedField
                label={translate('kalitronFurnitureStudioApp.cabinet.positionZ')}
                id="cabinet-positionZ"
                name="positionZ"
                data-cy="positionZ"
                type="text"
              />
              <ValidatedField
                label={translate('kalitronFurnitureStudioApp.cabinet.rotationDeg')}
                id="cabinet-rotationDeg"
                name="rotationDeg"
                data-cy="rotationDeg"
                type="text"
              />
              <ValidatedField
                label={translate('kalitronFurnitureStudioApp.cabinet.positionSeq')}
                id="cabinet-positionSeq"
                name="positionSeq"
                data-cy="positionSeq"
                type="text"
              />
              <ValidatedField
                label={translate('kalitronFurnitureStudioApp.cabinet.csvRowJson')}
                id="cabinet-csvRowJson"
                name="csvRowJson"
                data-cy="csvRowJson"
                type="textarea"
              />
              <ValidatedField
                label={translate('kalitronFurnitureStudioApp.cabinet.notes')}
                id="cabinet-notes"
                name="notes"
                data-cy="notes"
                type="text"
                validate={{
                  maxLength: { value: 300, message: translate('entity.validation.maxlength', { max: 300 }) },
                }}
              />
              <ValidatedField
                id="cabinet-template"
                name="template"
                data-cy="template"
                label={translate('kalitronFurnitureStudioApp.cabinet.template')}
                type="select"
              >
                <option value="" key="0" />
                {cabinetTemplates
                  ? cabinetTemplates.map(otherEntity => (
                      <option value={otherEntity.id} key={otherEntity.id}>
                        {otherEntity.code}
                      </option>
                    ))
                  : null}
              </ValidatedField>
              <ValidatedField
                id="cabinet-material"
                name="material"
                data-cy="material"
                label={translate('kalitronFurnitureStudioApp.cabinet.material')}
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
                id="cabinet-spec"
                name="spec"
                data-cy="spec"
                label={translate('kalitronFurnitureStudioApp.cabinet.spec')}
                type="select"
                required
              >
                <option value="" key="0" />
                {kitchenSpecs
                  ? kitchenSpecs.map(otherEntity => (
                      <option value={otherEntity.id} key={otherEntity.id}>
                        {otherEntity.style}
                      </option>
                    ))
                  : null}
              </ValidatedField>
              <FormText>
                <Translate contentKey="entity.validation.required">This field is required.</Translate>
              </FormText>
              <Button as={Link as any} id="cancel-save" data-cy="entityCreateCancelButton" to="/cabinet" replace variant="info">
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

export default CabinetUpdate;
