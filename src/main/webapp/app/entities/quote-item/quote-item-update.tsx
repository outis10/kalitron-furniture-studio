import React, { useEffect } from 'react';
import { Button, Col, FormText, Row } from 'react-bootstrap';
import { Translate, ValidatedField, ValidatedForm, isNumber, translate } from 'react-jhipster';
import { Link, useNavigate, useParams } from 'react-router';

import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { useAppDispatch, useAppSelector } from 'app/config/store';
import { getEntities as getCabinets } from 'app/entities/cabinet/cabinet.reducer';
import { getEntities as getHardwares } from 'app/entities/hardware/hardware.reducer';
import { getEntities as getQuotes } from 'app/entities/quote/quote.reducer';

import { createEntity, getEntity, reset, updateEntity } from './quote-item.reducer';

export const QuoteItemUpdate = () => {
  const dispatch = useAppDispatch();

  const navigate = useNavigate();

  const { id } = useParams<'id'>();
  const isNew = id === undefined;

  const cabinets = useAppSelector(state => state.cabinet.entities);
  const hardwares = useAppSelector(state => state.hardware.entities);
  const quotes = useAppSelector(state => state.quote.entities);
  const quoteItemEntity = useAppSelector(state => state.quoteItem.entity);
  const loading = useAppSelector(state => state.quoteItem.loading);
  const updating = useAppSelector(state => state.quoteItem.updating);
  const updateSuccess = useAppSelector(state => state.quoteItem.updateSuccess);

  const handleClose = () => {
    navigate(`/quote-item${location.search}`);
  };

  useEffect(() => {
    if (isNew) {
      dispatch(reset());
    } else {
      dispatch(getEntity(id));
    }

    dispatch(getCabinets({}));
    dispatch(getHardwares({}));
    dispatch(getQuotes({}));
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
    if (values.quantity !== undefined && typeof values.quantity !== 'number') {
      values.quantity = Number(values.quantity);
    }
    if (values.unitPriceMxn !== undefined && typeof values.unitPriceMxn !== 'number') {
      values.unitPriceMxn = Number(values.unitPriceMxn);
    }
    if (values.totalMxn !== undefined && typeof values.totalMxn !== 'number') {
      values.totalMxn = Number(values.totalMxn);
    }

    const entity = {
      ...quoteItemEntity,
      ...values,
      cabinet: cabinets.find(it => it.id.toString() === values.cabinet?.toString()),
      hardware: hardwares.find(it => it.id.toString() === values.hardware?.toString()),
      quote: quotes.find(it => it.id.toString() === values.quote?.toString()),
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
          ...quoteItemEntity,
          cabinet: quoteItemEntity?.cabinet?.id,
          hardware: quoteItemEntity?.hardware?.id,
          quote: quoteItemEntity?.quote?.id,
        };

  return (
    <div>
      <Row className="justify-content-center">
        <Col md="8">
          <h2 id="kalitronFurnitureStudioApp.quoteItem.home.createOrEditLabel" data-cy="QuoteItemCreateUpdateHeading">
            <Translate contentKey="kalitronFurnitureStudioApp.quoteItem.home.createOrEditLabel">Create or edit a QuoteItem</Translate>
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
                  id="quote-item-id"
                  label={translate('global.field.id')}
                  validate={{ required: true }}
                />
              )}
              <ValidatedField
                label={translate('kalitronFurnitureStudioApp.quoteItem.description')}
                id="quote-item-description"
                name="description"
                data-cy="description"
                type="text"
                validate={{
                  required: { value: true, message: translate('entity.validation.required') },
                  maxLength: { value: 200, message: translate('entity.validation.maxlength', { max: 200 }) },
                }}
              />
              <ValidatedField
                label={translate('kalitronFurnitureStudioApp.quoteItem.quantity')}
                id="quote-item-quantity"
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
                label={translate('kalitronFurnitureStudioApp.quoteItem.unitPriceMxn')}
                id="quote-item-unitPriceMxn"
                name="unitPriceMxn"
                data-cy="unitPriceMxn"
                type="text"
                validate={{
                  required: { value: true, message: translate('entity.validation.required') },
                  validate: v => isNumber(v) || translate('entity.validation.number'),
                }}
              />
              <ValidatedField
                label={translate('kalitronFurnitureStudioApp.quoteItem.totalMxn')}
                id="quote-item-totalMxn"
                name="totalMxn"
                data-cy="totalMxn"
                type="text"
                validate={{
                  required: { value: true, message: translate('entity.validation.required') },
                  validate: v => isNumber(v) || translate('entity.validation.number'),
                }}
              />
              <ValidatedField
                label={translate('kalitronFurnitureStudioApp.quoteItem.category')}
                id="quote-item-category"
                name="category"
                data-cy="category"
                type="text"
                validate={{
                  maxLength: { value: 60, message: translate('entity.validation.maxlength', { max: 60 }) },
                }}
              />
              <ValidatedField
                label={translate('kalitronFurnitureStudioApp.quoteItem.notes')}
                id="quote-item-notes"
                name="notes"
                data-cy="notes"
                type="text"
                validate={{
                  maxLength: { value: 200, message: translate('entity.validation.maxlength', { max: 200 }) },
                }}
              />
              <ValidatedField
                id="quote-item-cabinet"
                name="cabinet"
                data-cy="cabinet"
                label={translate('kalitronFurnitureStudioApp.quoteItem.cabinet')}
                type="select"
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
              <ValidatedField
                id="quote-item-hardware"
                name="hardware"
                data-cy="hardware"
                label={translate('kalitronFurnitureStudioApp.quoteItem.hardware')}
                type="select"
              >
                <option value="" key="0" />
                {hardwares
                  ? hardwares.map(otherEntity => (
                      <option value={otherEntity.id} key={otherEntity.id}>
                        {otherEntity.name}
                      </option>
                    ))
                  : null}
              </ValidatedField>
              <ValidatedField
                id="quote-item-quote"
                name="quote"
                data-cy="quote"
                label={translate('kalitronFurnitureStudioApp.quoteItem.quote')}
                type="select"
                required
              >
                <option value="" key="0" />
                {quotes
                  ? quotes.map(otherEntity => (
                      <option value={otherEntity.id} key={otherEntity.id}>
                        {otherEntity.quoteNumber}
                      </option>
                    ))
                  : null}
              </ValidatedField>
              <FormText>
                <Translate contentKey="entity.validation.required">This field is required.</Translate>
              </FormText>
              <Button as={Link as any} id="cancel-save" data-cy="entityCreateCancelButton" to="/quote-item" replace variant="info">
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

export default QuoteItemUpdate;
