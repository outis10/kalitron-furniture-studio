import React, { useEffect } from 'react';
import { Button, Col, FormText, Row } from 'react-bootstrap';
import { Translate, ValidatedField, ValidatedForm, isNumber, translate } from 'react-jhipster';
import { Link, useNavigate, useParams } from 'react-router';

import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { useAppDispatch, useAppSelector } from 'app/config/store';
import { getEntities as getDesignArtifacts } from 'app/entities/design-artifact/design-artifact.reducer';
import { getEntities as getDesignImages } from 'app/entities/design-image/design-image.reducer';
import { getEntities as getDesignSessions } from 'app/entities/design-session/design-session.reducer';
import { QuoteStatus } from 'app/shared/model/enumerations/quote-status.model';
import { convertDateTimeFromServer, convertDateTimeToServer, displayDefaultDateTime } from 'app/shared/util/date-utils';

import { createEntity, getEntity, reset, updateEntity } from './quote.reducer';

export const QuoteUpdate = () => {
  const dispatch = useAppDispatch();

  const navigate = useNavigate();

  const { id } = useParams<'id'>();
  const isNew = id === undefined;

  const designImages = useAppSelector(state => state.designImage.entities);
  const designArtifacts = useAppSelector(state => state.designArtifact.entities);
  const designSessions = useAppSelector(state => state.designSession.entities);
  const quoteEntity = useAppSelector(state => state.quote.entity);
  const loading = useAppSelector(state => state.quote.loading);
  const updating = useAppSelector(state => state.quote.updating);
  const updateSuccess = useAppSelector(state => state.quote.updateSuccess);
  const quoteStatusValues = Object.keys(QuoteStatus);

  const handleClose = () => {
    navigate(`/quote${location.search}`);
  };

  useEffect(() => {
    if (isNew) {
      dispatch(reset());
    } else {
      dispatch(getEntity(id));
    }

    dispatch(getDesignImages({}));
    dispatch(getDesignArtifacts({}));
    dispatch(getDesignSessions({}));
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
    if (values.subtotalMxn !== undefined && typeof values.subtotalMxn !== 'number') {
      values.subtotalMxn = Number(values.subtotalMxn);
    }
    if (values.taxMxn !== undefined && typeof values.taxMxn !== 'number') {
      values.taxMxn = Number(values.taxMxn);
    }
    if (values.totalMxn !== undefined && typeof values.totalMxn !== 'number') {
      values.totalMxn = Number(values.totalMxn);
    }
    if (values.laborMxn !== undefined && typeof values.laborMxn !== 'number') {
      values.laborMxn = Number(values.laborMxn);
    }
    values.createdAt = convertDateTimeToServer(values.createdAt);
    values.sentAt = convertDateTimeToServer(values.sentAt);

    const entity = {
      ...quoteEntity,
      ...values,
      renderImage: designImages.find(it => it.id.toString() === values.renderImage?.toString()),
      pdfArtifact: designArtifacts.find(it => it.id.toString() === values.pdfArtifact?.toString()),
      session: designSessions.find(it => it.id.toString() === values.session?.toString()),
    };

    if (isNew) {
      dispatch(createEntity(entity));
    } else {
      dispatch(updateEntity(entity));
    }
  };

  const defaultValues = () =>
    isNew
      ? {
          createdAt: displayDefaultDateTime(),
          sentAt: displayDefaultDateTime(),
        }
      : {
          status: 'DRAFT',
          ...quoteEntity,
          createdAt: convertDateTimeFromServer(quoteEntity.createdAt),
          sentAt: convertDateTimeFromServer(quoteEntity.sentAt),
          renderImage: quoteEntity?.renderImage?.id,
          pdfArtifact: quoteEntity?.pdfArtifact?.id,
          session: quoteEntity?.session?.id,
        };

  return (
    <div>
      <Row className="justify-content-center">
        <Col md="8">
          <h2 id="kalitronFurnitureStudioApp.quote.home.createOrEditLabel" data-cy="QuoteCreateUpdateHeading">
            <Translate contentKey="kalitronFurnitureStudioApp.quote.home.createOrEditLabel">Create or edit a Quote</Translate>
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
                  id="quote-id"
                  label={translate('global.field.id')}
                  validate={{ required: true }}
                />
              )}
              <ValidatedField
                label={translate('kalitronFurnitureStudioApp.quote.quoteNumber')}
                id="quote-quoteNumber"
                name="quoteNumber"
                data-cy="quoteNumber"
                type="text"
                validate={{
                  required: { value: true, message: translate('entity.validation.required') },
                  maxLength: { value: 20, message: translate('entity.validation.maxlength', { max: 20 }) },
                }}
              />
              <ValidatedField
                label={translate('kalitronFurnitureStudioApp.quote.status')}
                id="quote-status"
                name="status"
                data-cy="status"
                type="select"
              >
                {quoteStatusValues.map(quoteStatus => (
                  <option value={quoteStatus} key={quoteStatus}>
                    {translate(`kalitronFurnitureStudioApp.QuoteStatus.${quoteStatus}`)}
                  </option>
                ))}
              </ValidatedField>
              <ValidatedField
                label={translate('kalitronFurnitureStudioApp.quote.subtotalMxn')}
                id="quote-subtotalMxn"
                name="subtotalMxn"
                data-cy="subtotalMxn"
                type="text"
                validate={{
                  required: { value: true, message: translate('entity.validation.required') },
                  validate: v => isNumber(v) || translate('entity.validation.number'),
                }}
              />
              <ValidatedField
                label={translate('kalitronFurnitureStudioApp.quote.taxMxn')}
                id="quote-taxMxn"
                name="taxMxn"
                data-cy="taxMxn"
                type="text"
                validate={{
                  required: { value: true, message: translate('entity.validation.required') },
                  validate: v => isNumber(v) || translate('entity.validation.number'),
                }}
              />
              <ValidatedField
                label={translate('kalitronFurnitureStudioApp.quote.totalMxn')}
                id="quote-totalMxn"
                name="totalMxn"
                data-cy="totalMxn"
                type="text"
                validate={{
                  required: { value: true, message: translate('entity.validation.required') },
                  validate: v => isNumber(v) || translate('entity.validation.number'),
                }}
              />
              <ValidatedField
                label={translate('kalitronFurnitureStudioApp.quote.laborMxn')}
                id="quote-laborMxn"
                name="laborMxn"
                data-cy="laborMxn"
                type="text"
              />
              <ValidatedField
                label={translate('kalitronFurnitureStudioApp.quote.validUntil')}
                id="quote-validUntil"
                name="validUntil"
                data-cy="validUntil"
                type="date"
              />
              <ValidatedField
                label={translate('kalitronFurnitureStudioApp.quote.publicToken')}
                id="quote-publicToken"
                name="publicToken"
                data-cy="publicToken"
                type="text"
                validate={{
                  maxLength: { value: 80, message: translate('entity.validation.maxlength', { max: 80 }) },
                }}
              />
              <ValidatedField
                label={translate('kalitronFurnitureStudioApp.quote.notes')}
                id="quote-notes"
                name="notes"
                data-cy="notes"
                type="text"
                validate={{
                  maxLength: { value: 1000, message: translate('entity.validation.maxlength', { max: 1000 }) },
                }}
              />
              <ValidatedField
                label={translate('kalitronFurnitureStudioApp.quote.createdAt')}
                id="quote-createdAt"
                name="createdAt"
                data-cy="createdAt"
                type="datetime-local"
                placeholder="YYYY-MM-DD HH:mm"
                validate={{
                  required: { value: true, message: translate('entity.validation.required') },
                }}
              />
              <ValidatedField
                label={translate('kalitronFurnitureStudioApp.quote.sentAt')}
                id="quote-sentAt"
                name="sentAt"
                data-cy="sentAt"
                type="datetime-local"
                placeholder="YYYY-MM-DD HH:mm"
              />
              <ValidatedField
                id="quote-renderImage"
                name="renderImage"
                data-cy="renderImage"
                label={translate('kalitronFurnitureStudioApp.quote.renderImage')}
                type="select"
              >
                <option value="" key="0" />
                {designImages
                  ? designImages.map(otherEntity => (
                      <option value={otherEntity.id} key={otherEntity.id}>
                        {otherEntity.fileName}
                      </option>
                    ))
                  : null}
              </ValidatedField>
              <ValidatedField
                id="quote-pdfArtifact"
                name="pdfArtifact"
                data-cy="pdfArtifact"
                label={translate('kalitronFurnitureStudioApp.quote.pdfArtifact')}
                type="select"
              >
                <option value="" key="0" />
                {designArtifacts
                  ? designArtifacts.map(otherEntity => (
                      <option value={otherEntity.id} key={otherEntity.id}>
                        {otherEntity.fileName}
                      </option>
                    ))
                  : null}
              </ValidatedField>
              <ValidatedField
                id="quote-session"
                name="session"
                data-cy="session"
                label={translate('kalitronFurnitureStudioApp.quote.session')}
                type="select"
                required
              >
                <option value="" key="0" />
                {designSessions
                  ? designSessions.map(otherEntity => (
                      <option value={otherEntity.id} key={otherEntity.id}>
                        {otherEntity.sessionCode}
                      </option>
                    ))
                  : null}
              </ValidatedField>
              <FormText>
                <Translate contentKey="entity.validation.required">This field is required.</Translate>
              </FormText>
              <Button as={Link as any} id="cancel-save" data-cy="entityCreateCancelButton" to="/quote" replace variant="info">
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

export default QuoteUpdate;
