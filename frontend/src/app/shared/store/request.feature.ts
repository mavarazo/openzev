import { computed, effect } from '@angular/core';
import {
  signalStoreFeature,
  withComputed,
  withHooks,
  withState,
} from '@ngrx/signals';
import { HttpErrorResponse } from '@angular/common/http';

export type RequestStatus =
  | 'idle'
  | 'pending'
  | 'fulfilled'
  | { status: number; message: String };
export type RequestStatusState = { requestStatus: RequestStatus };

export function withRequestStatus() {
  return signalStoreFeature(
    withState<RequestStatusState>({ requestStatus: 'idle' }),
    withComputed(({ requestStatus }) => ({
      isPending: computed(() => requestStatus() === 'pending'),
      isFulfilled: computed(() => requestStatus() === 'fulfilled'),
      error: computed(() => {
        const status = requestStatus();
        return typeof status === 'object' ? status : null;
      }),
    })),
    withHooks({
      onInit(store) {
        effect(() => {
          console.log(`request ${JSON.stringify(store.requestStatus())}`);
        });
      },
    }),
  );
}

export function setPending(): RequestStatusState {
  return { requestStatus: 'pending' };
}

export function setFulfilled(): RequestStatusState {
  return { requestStatus: 'fulfilled' };
}

export function setError(
  error: HttpErrorResponse,
  message?: String,
): RequestStatusState {
  const msg = message ?? error.statusText;
  return { requestStatus: { status: error.status, message: msg } };
}
